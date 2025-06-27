module.exports = {
    "branches": ["main"],
    "plugins": [
        "@semantic-release/commit-analyzer",
        "@semantic-release/release-notes-generator",
        [
            "@semantic-release/changelog",
            {
                "changelogFile": "docs/CHANGELOG.md"
            }
        ],
        "@semantic-release/github",
        [
            "@semantic-release/git",
            {
                "assets": [
                    "docs/CHANGELOG.md",
                    "pom.xml"
                ],
                "message": "chore(release): ${nextRelease.version}"
            }
        ],
        [
            "@semantic-release/exec",
            {
                "prepareCmd": "./scripts/update-pom-version.sh ${nextRelease.version}"
            }
        ],
        [
            "@codedependant/semantic-release-docker",  // backend build
            {
                "dockerTags": [
                    "latest",
                    "{{version}}",
                    "{{major}}.{{minor}}",

                    process.env.BRANCH_NAME
                ],
                "dockerImage": process.env.BACKEND_IMAGE_NAME,
                "dockerFile": "./backend/Dockerfile",
                "dockerRegistry": process.env.REGISTRY,
                "dockerProject": process.env.BACKEND_IMAGE_PROJECT,
                "dockerContext": "./backend/",
                "dockerPlatform": ["linux/amd64"],
            }
        ],
        [
            "@codedependant/semantic-release-docker",  // worker build
            {
                "dockerTags": [
                    "latest",
                    "{{version}}",
                    "{{major}}.{{minor}}",

                    process.env.BRANCH_NAME
                ],
                "dockerImage": process.env.WORKER_IMAGE_NAME,
                "dockerFile": "./worker/Dockerfile",
                "dockerRegistry": process.env.REGISTRY,
                "dockerProject": process.env.WORKER_IMAGE_PROJECT,
                "dockerContext": "./worker/",
                "dockerPlatform": ["linux/amd64"],
            }
        ],
    ]
}