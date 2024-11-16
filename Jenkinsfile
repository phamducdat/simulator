#!/usr/bin/env groovy

node {
    properties([disableConcurrentBuilds()])

    try {
        project = "lotusmile-account-service"
        dockerRepo = "dockerhub.infra.wiinvent.tv"
        imagePrefix = "ci"
        dockerFile = "Dockerfile"
        imageName = "${dockerRepo}/${imagePrefix}/${project}"
        buildNumber = "${env.BUILD_NUMBER}"
        IMAGE_BUILD = "${imageName}:${env.BRANCH_NAME}-build-${buildNumber}"
        k8sCluster = "local"
        k8sNameSpace = "lotusmile-prod"
        k8sEnv = "production"

        stage('checkout code') {
            checkout scm
            sh "git checkout ${env.BRANCH_NAME} && git reset --hard origin/${env.BRANCH_NAME}"
        }

        stage('build') {
            sh """
                egrep -q '^FROM .* AS builder\$' ${dockerFile} \
                && DOCKER_BUILDKIT=1 docker build -t ${imageName}-stage-builder --target builder -f ${dockerFile} .
                DOCKER_BUILDKIT=1 docker build -t ${imageName}:${env.BRANCH_NAME} -f ${dockerFile} .
            """
        }
        stage('push') {
            sh """
                docker push ${imageName}:${env.BRANCH_NAME}
                docker tag ${imageName}:${env.BRANCH_NAME} ${imageName}:${env.BRANCH_NAME}-build-${buildNumber}
                docker push ${imageName}:${env.BRANCH_NAME}-build-${buildNumber}
            """
        }
        switch (env.BRANCH_NAME) {
            case 'develop':
                k8sNameSpace = "lotusmile-dev"
                k8sEnv = "development"
                stage('deploy-prod') {
                    sh """
                    ## Deploy cluster LongVan
                    /usr/local/k8s/bin/k8sctl --cluster-name=${k8sCluster} --namespace=${k8sNameSpace} --environment=${k8sEnv} --service-name=${project} --image-name=${IMAGE_BUILD}
                  """
                }
                break
            case 'master':
                stage('deploy-prod') {
                    sh """
                    ## Deploy cluster LongVan
                    /usr/local/k8s/bin/k8sctl --cluster-name=${k8sCluster} --namespace=${k8sNameSpace} --environment=${k8sEnv} --service-name=${project} --image-name=${IMAGE_BUILD}
                  """
                }
                break

        }

    } catch (e) {
        currentBuild.result = "FAILED"
        throw e
    }
}
