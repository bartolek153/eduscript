package org.eduscript.services;

import org.eduscript.configs.kubernetes.KubernetesProperties;
import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

@Component
public class KubernetesClientFactory {

    private final KubernetesProperties props;

    public KubernetesClientFactory(KubernetesProperties kubernetesProperties) {
        props = kubernetesProperties;
    }

    public KubernetesClient createClient() {
        Config cfg = new ConfigBuilder()
                .withMasterUrl(props.getMasterUrl())
                .withTrustCerts(props.getTrustCerts())
                .withNamespace(props.getNamespace())
                .build();

        return createClient(cfg);
    }

    public KubernetesClient createClient(Config config) {
        return new KubernetesClientBuilder()
                .withConfig(config)
                .build();
    }

    public KubernetesClient createInClusterClient() {
        Config config = new ConfigBuilder()
                .build();
        return new KubernetesClientBuilder()
                .withConfig(config)
                .build();
    }

    public KubernetesClient createClientFromKubeconfig(String kubeconfigPath) {
        Config config = Config.fromKubeconfig(kubeconfigPath);
        return new KubernetesClientBuilder()
                .withConfig(config)
                .build();
    }

    public KubernetesClient createClientWithToken(String masterUrl, String token) {
        Config config = new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withOauthToken(token)
                .build();
        return new KubernetesClientBuilder()
                .withConfig(config)
                .build();
    }
}
