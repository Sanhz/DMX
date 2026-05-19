# Plantilla de pipeline genérica para Azure (Azure DevOps / GitHub Actions - orientación)

Este documento contiene una propuesta para construir y desplegar el microservicio (Spring Boot / Maven / Docker) en Azure.

Objetivos del pipeline (lo que yo consideraría hacer en un pipeline enterprise con ambientes de desarrollo, preproducción y producción):
- Compilar y ejecutar tests (unitarios y, opcionalmente, integración).
- Ejecutar análisis estático (SonarQube) si aplica.
- Construir imagen Docker y desplegar a un registro (Azure Container Registry — ACR).
- Desplegar al entorno objetivo (AKS o Azure Web App for Containers).

Arquitectura recomendada (genérica)
- Repositorio Git (branching: feature/develop/pre/main)
- Azure DevOps Pipelines / GitHub Actions para CI/CD
- Azure Container Registry (ACR) para imágenes Docker
- AKS (Kubernetes) o Azure App Service for Containers para ejecución
- Opcional: Azure Key Vault para secretos

YAML: pipeline genérico (Azure DevOps / se puede adaptar a GitHub Actions)

```
# --- Variables / parametrización ---
trigger:
  branches:
    include:
      - develop
      - pre
      - main

variables:
  MAVEN_OPTS: '-Xmx1024m'
  MAVEN_CACHE: ~/.m2
  IMAGE_NAME: 'dmx-credit-app'
  ACR_NAME: '<<ACR_NAME>>'          # ejemplo: dmxRegistry
  ACR_RESOURCE_GROUP: '<<RESOURCE_GROUP>>'
  ACR_LOGIN_SERVER: '<<ACR_LOGIN_SERVER>>' # ejemplo: dmxRegistry.azurecr.io
  K8S_NAMESPACE: 'default'         # o el namespace por entorno
  K8S_DEPLOYMENT: 'dmx-credit-app'

pool:
  vmImage: 'ubuntu-latest'

stages:
  # --- Etapa: Compilar y ejecutar pruebas ---
  - stage: Compilar
    displayName: 'Compilar y ejecutar pruebas'
    jobs:
      - job: Build
        displayName: 'Compile and test'
        steps:
          - task: Checkout@1

          - name: Set up JDK 21
            uses: actions/setup-java@v4
            with:
              java-version: '21'
              distribution: 'temurin'
# --- Considerar omitir pruebas ya que se evalúan en el stage de Calidad (SonarQube) ---
          - script: |
              mvn -B -DskipTests=false clean package
            displayName: 'Maven build and tests'

          - task: PublishBuildArtifacts@1
            inputs:
              PathtoPublish: 'target'
              ArtifactName: 'drop'

  # --- Etapa: Calidad de código (Sonar) ---
  - stage: Calidad
    displayName: 'Sonarqube'
    dependsOn: Build
    condition: succeeded()
    jobs:
      - job: Sonar
        displayName: 'Análisis de SonarQube'
        steps:
          - task: SonarQubePrepare@5
            inputs:
              SonarQube: '<<SONAR_SERVICE_CONNECTION>>' # service connection name
              scannerMode: 'CLI'
              configMode: 'manual'
              cliProjectKey: 'dmx-credit-app'

          - script: mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594:sonar
            displayName: 'Run SonarQube'

          - task: SonarQubePublish@5
            inputs:
              pollingTimeoutSec: '300'
# --- Considerar detener o no el pipeline en función del ambiente en el que se esté desplegando (ej: detener en pre/prod si falla el Quality Gate) ---
#             failIfQualityGateFails: true
  # --- Etapa: Publicar imagen Docker ---
  - stage: PublicarImagen
    displayName: 'Construir y publicar imagen Docker'
    dependsOn: Quality
    condition: succeeded()
    jobs:
      - job: DockerBuild
        displayName: 'Compilar y publicar'
        steps:
          - task: Docker@2
            displayName: 'Compilar imagen Docker y publicar en ACR'
            inputs:
              command: buildAndPush
              containerRegistry: '<<ACR_SERVICE_CONNECTION>>' # service connection to ACR
              repository: $(ACR_LOGIN_SERVER)/$(IMAGE_NAME)
              tags: '$(Build.BuildId)'

  # --- Etapa: Despliegue en AKS ---
  - stage: DespliegueAKS
    displayName: 'Desplegar en AKS'
    dependsOn: PublishImage
    condition: succeeded()
    jobs:
      - deployment: DespliegueAKS
        displayName: 'Despliegue en AKS (Azure Kubernetes Service)'
        environment: '<<environment-name>>'
        strategy:
          runOnce:
            deploy:
              steps:
                # Obtener credenciales de AKS y actualizar la imagen del despliegue
                - task: AzureCLI@2
                  displayName: 'Obtener credenciales AKS y actualizar imagen'
                  inputs:
                    azureSubscription: '<<AZURE_SERVICE_CONNECTION>>' # Service connection (SPN) with access to AKS & ACR
                    scriptType: 'bash'
                    scriptLocation: 'inlineScript'
                    inlineScript: |
                      set -e
                      echo "Accediendo a ACR (opcional si AKS tiene acceso directo a ACR)"
                      # opcional: az acr login --name <<ACR_NAME>>
                      echo "Obteniendo credenciales de AKS"
                      az aks get-credentials --resource-group <<AKS_RESOURCE_GROUP>> --name <<AKS_CLUSTER_NAME>> --admin

                      echo "Actualizando imagen para el despliegue: $(K8S_DEPLOYMENT)"
                      kubectl -n $(K8S_NAMESPACE) set image deployment/$(K8S_DEPLOYMENT) $(K8S_DEPLOYMENT)=$(ACR_LOGIN_SERVER)/$(IMAGE_NAME):$(Build.BuildId) --record || true

                - script: |
                    echo "Despliegue a AKS finalizado."
                  displayName: 'Finalizar despliegue'

  # --- Etapa: Notificaciones posteriores al lanzamiento ---
  - stage: NotificacionesPostLanzamiento
    displayName: 'Notificaciones post-lanzamiento'
    dependsOn: Deploy
    condition: always()
    jobs:
      - job: Notificar
        steps:
          - script: echo "Despliegue finalizado. Build: $(Build.BuildId)"

```



Snippet (GitHub Actions) - build/push y despliegue en AKS

```
# Compilar y publicar en ACR
- name: Acceso a ACR
  uses: azure/docker-login@v1
  with:
    login-server: ${{ secrets.ACR_LOGIN_SERVER }}
    username: ${{ secrets.ACR_USERNAME }}
    password: ${{ secrets.ACR_PASSWORD }}

- name: Compilar y publicar imagen Docker
  run: |
    docker build -t ${{ secrets.ACR_LOGIN_SERVER }}/dmx-credit-app:${{ github.sha }} .
    docker push ${{ secrets.ACR_LOGIN_SERVER }}/dmx-credit-app:${{ github.sha }}

# Obtener credenciales AKS y actualizar imagen del Deployment
- name: Acceso a Azure
  uses: azure/login@v1
  with:
    creds: ${{ secrets.AZURE_CREDENTIALS }}

- name: Obtener credenciales de AKS
  run: az aks get-credentials --resource-group ${{ secrets.AKS_RESOURCE_GROUP }} --name ${{ secrets.AKS_CLUSTER_NAME }} --admin

- name: Actualizar imagen del despliegue en AKS
  run: |
    kubectl -n ${{ env.K8S_NAMESPACE }} set image deployment/${{ env.K8S_DEPLOYMENT }} ${{ env.K8S_DEPLOYMENT }}=${{ secrets.ACR_LOGIN_SERVER }}/dmx-credit-app:${{ github.sha }} --record
```

Descripción breve de cada stage:
- Compilar: compila el proyecto y ejecuta tests. Publica artefactos.
- Calidad: ejecuta SonarQube y valida Quality Gate (opcionalmente frena el pipeline si falla).
- Publicar: construye la imagen Docker y la publica en ACR.
- Desplegar: despliega la imagen al entorno objetivo (AKS o App Service). Incluye opción de despliegue in-place actualizando la imagen del Deployment.
- Post-lanzamiento: notificaciones y limpieza.

