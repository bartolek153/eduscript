package org.eduscript.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eduscript.logging.Logger;
import org.eduscript.model.JobMessage;
import org.eduscript.model.JobTask;
import org.eduscript.services.KubernetesClientFactory;
import org.eduscript.services.SandboxExecutor;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.LogWatch;

@Service
public class SandboxExecutorImpl implements SandboxExecutor {

  private static final String DEFAULT = "default"; // TODO: make a parameter
  private static final int FINISHED_JOB_TTL = 60; // TODO: make a parameter

  private static final String JOB_ID = "jobId";

  private final KubernetesClientFactory clientFactory;

  public SandboxExecutorImpl(KubernetesClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  @Override
  public void execute(JobMessage msg) {
    List<Container> initContainers = new ArrayList<>();

    for (JobTask task : msg.getTasks()) {
      initContainers.add(createContainer(task.getName(), task.getImage(), task.getArgs()));
    }

    PodSpec podSpec = new PodSpecBuilder()
        .withRestartPolicy("Never")
        .withInitContainers(initContainers)
        .withContainers(createContainer(
            "a",
            "hello-world",
            null))
        .build();

    Job job = createJob(msg.getId(), podSpec);

    try (KubernetesClient client = clientFactory.createClient()) {
      client.batch().v1().jobs()
          .inNamespace(DEFAULT)
          .resource(job)
          .create();
    } catch (KubernetesClientException ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }

    try {
      streamInitContainersLogs(DEFAULT, msg.getId().toString());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private Container createContainer(String name, String image, Map<String, String> envs) {
    ContainerBuilder containerBuilder = new ContainerBuilder()
        .withName(name)
        .withImage(image);

    // Add environment variables if provided
    if (envs != null && !envs.isEmpty()) {
      List<EnvVar> envVars = new ArrayList<>();
      for (Map.Entry<String, String> entry : envs.entrySet()) {
        envVars.add(new EnvVar(entry.getKey(), entry.getValue(), null));
      }
      containerBuilder.withEnv(envVars);
    }

    return containerBuilder.build();
  }

  private Job createJob(UUID jobId, PodSpec podSpec) {
    String jobIdStr = jobId.toString();
    String jobName = "execute-" + jobIdStr;

    ObjectMeta jobMetadata = new ObjectMetaBuilder()
        .withName(jobName)
        .build();

    ObjectMeta podMetadata = new ObjectMetaBuilder()
        .addToLabels(JOB_ID, jobIdStr)
        .build();

    PodTemplateSpec podTemplate = new PodTemplateSpecBuilder()
        .withMetadata(podMetadata)
        .withSpec(podSpec)
        .build();

    return new JobBuilder()
        .withMetadata(jobMetadata)
        .withNewSpec()
        .withTtlSecondsAfterFinished(FINISHED_JOB_TTL)
        .withTemplate(podTemplate)
        .endSpec()
        .build();
  }

  private void streamInitContainersLogs(String namespace, String jobId) throws Exception {
    try (KubernetesClient client = clientFactory.createClient()) {
      // Find pods with the job label
      PodList podList = client.pods()
          .inNamespace(namespace)
          .withLabel(JOB_ID, jobId)
          .list();

      if (podList.getItems().isEmpty()) {
        System.out.println("No pods found for job: " + jobId);
        return;
      }

      Pod pod = podList.getItems().get(0);
      String podName = pod.getMetadata().getName();
      List<Container> initContainers = pod.getSpec().getInitContainers();

      if (initContainers == null || initContainers.isEmpty()) {
        System.out.println("No init containers found in pod: " + podName);
        return;
      }

      for (Container container : initContainers) {
        String containerName = container.getName();
        Logger.printWarning("▶ Waiting for initContainer: %s%n", containerName);

        waitForContainerStartOrTerminate(client, namespace, podName, containerName, true);

        Logger.printWarning("▶ Streaming logs from: %s%n", containerName);
        streamContainerLogs(client, namespace, podName, containerName);
        Logger.printSuccess("▶ Container completed");
      }
    } catch (Exception ex) {
      // TODO: handle exception properly
      ex.printStackTrace();
    }
  }

  private void waitForContainerStartOrTerminate(KubernetesClient client, String namespace,
      String podName, String containerName, boolean isInit) throws Exception {

    while (true) {
      Pod pod = client.pods()
          .inNamespace(namespace)
          .withName(podName)
          .get();

      if (pod == null) {
        throw new RuntimeException("Pod not found: " + podName);
      }

      List<ContainerStatus> statuses = isInit
          ? pod.getStatus().getInitContainerStatuses()
          : pod.getStatus().getContainerStatuses();

      if (statuses != null) {
        for (ContainerStatus status : statuses) {
          if (status.getName().equals(containerName)) {
            if (Boolean.TRUE.equals(status.getStarted()) ||
                status.getState().getTerminated() != null) {
              return;
            }
          }
        }
      }

      Thread.sleep(1000);
    }
  }

  private void streamContainerLogs(KubernetesClient client, String namespace,
      String podName, String containerName) throws Exception {

    try (LogWatch logWatch = client.pods()
        .inNamespace(namespace)
        .withName(podName)
        .inContainer(containerName)
        .watchLog()) {

      // Read logs from the LogWatch InputStream
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(logWatch.getOutput()))) {

        String line;
        while ((line = reader.readLine()) != null) {
          // System.out.println(line);
          Logger.printInfo(line);
        }
      }
    }
  }

  private void waitForJobCompletion(String namespace, String jobName, long timeoutMinutes)
      throws Exception {

    try (KubernetesClient client = clientFactory.createClient()) {
      client.batch().v1().jobs()
          .inNamespace(namespace)
          .withName(jobName)
          .waitUntilCondition(job -> {
            if (job.getStatus() == null)
              return false;
            Integer succeeded = job.getStatus().getSucceeded();
            Integer failed = job.getStatus().getFailed();
            return (succeeded != null && succeeded > 0) ||
                (failed != null && failed > 0);
          }, timeoutMinutes, TimeUnit.MINUTES);
    }
  }
}