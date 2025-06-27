module.exports = {
    "branches": "main",
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
                    "{{env.GITHU_REF}}"
                ],
                "dockerImage": "{{env.GITHU_REF}}",
                "dockerFile": "Dockerfile",
                "dockerRegistry": "{{env.REGISTRY}}",
                "dockerProject": "{{env.GITHUB_REPOSITORY_OWNER}}",
                "dockerPlatform": ["linux/amd64"],
                "dockerBuildFlags": {
                    "target": "release"
                },
            }
        ],
    ]
}