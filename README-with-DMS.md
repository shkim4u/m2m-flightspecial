# M2M FlightSpecial Microservice

Monolith 예제인 TravelBuddy 애플리케이션에서 Flight 부분을 분해하여 구현한 마이크로서비스 예제입니다.

## Agenda - Day2

- 이론: Microservice란?
- [Strangler Fig Pattern](./docs/strangler-fig.md) - FlightSpecial 서비스
  - [Docker Compose](./docs/compose.md)
  - [Package 구조](./docs/package.md)
- 실습: [FlightSpecials](https://github.com/shkim4u/m2m-flightspecial) 애플리케이션을 분리
  - Progressive Delivery with ArgoCD
  - Canary Deployment with Argo Rollouts
- 부록: [API Gateway](./docs/apigw.md)
- 부록: [CQRS 패턴](./docs/cqrs.md)

## 0. Day 2 자원 배포하기
우리는 이미 네트워크, EKS, 레거시 데이터베이스 (MySQL)과 같은 Day 1 자원을 배포하였고, 여기에서 모놀리스 어플리케이션을 살펴보았습니다.<br>

이번에는 마이크로서비스 분리를 위하여 Day 2 자원을 배포해 보도록 하겠습니다.<br>
```bash
cd ~/environment/m2m-travelbuddy/infrastructure

# Private CA ARN을 설정합니다. 이는 ALB 설정에 필요합니다.
export CA_ARN_QUOTED=`aws acm-pca list-certificate-authorities --query 'CertificateAuthorities[?Status==\`ACTIVE\`].Arn | [0]'`
export CA_ARN=`echo $CA_ARN_QUOTED | tr -d '"'`
echo $CA_ARN

npm run day2
```


## 1. 로컬에서 둘러보기 가이드

### 데이터베이스 실행

1. (optional) 컨테이너 볼륨 준비

   > 'db'에 컨테이너 볼륨을 사용하고자 한다면 (호스트의 폴더를 마운트하지 않고)

   ```bash
   export DB_VOLUME="db-data"
   docker volume create ${DB_VOLUME}

   # Check
   docker volume ls | egrep "DRIVER|${DB_VOLUME}"
   ```

   > 컨테이너 볼륨을 지우거나 교체하려면

   ```bash
   # replace
   export DB_VOLUME="db-data"
   docker volume rm ${DB_VOLUME}
   docker volume create ${DB_VOLUME}
   ```

2. 데이터베이스 실행

   > (참고) ```docker-compose```가 설치되어 있지 않을 경우 아래 명령을 통해 먼저 설치한 후에 진행합니다.

   ```bash
   sudo curl -L "https://github.com/docker/compose/releases/download/v2.18.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   ```

   ```bash
   docker-compose up --build -d db

   # Check
   docker-compose ps
   ```

3. DB 스키마 적용(혹은 데이터셋 포함)

   ```bash
   docker-compose run --rm db-init
   ```

4. Clean-up (필요할 때만)

   ```bash
   docker-compose stop db
   docker-compose rm -f db

   # Check
   docker-compose ps -a
   ```

   volume (option)

   ```bash
   # 마운트한 호스트 폴더 정리
   rm -rf env/db/data

   # 컨테이너 볼륨 정리
   docker volume rm ${DB_VOLUME:-db-data}
   ```

### SpringBoot 애플리케이션 실행

#### A. 로컬에서 gradle 활용

> (참고) Java가 설치되어 있지 않을 경우 아래와 같이 Java를 설치합니다.
```bash
sudo yum install -y java-11-amazon-corretto-headless
```

```bash
# 1. Package
./gradlew clean build

# 2. Run Application
./gradlew :interface:bootRun
```

#### B. docker-compose 활용 컨테이너 프로세스로 실행

```bash
# 1. (Optional) 환경변수
export APP_PORT=8080 # default: 8080
export DB_VOLUME="db-data" # 컨테이너 볼륨을 사용하는 경우

# 2. 컨테이너 이미지 빌드 및 실행
docker-compose build app
docker-compose up -d app

# 3. 컨테이너 로그 확인
docker-compose logs -t -f --tail=50 app

# 4. 정리
docker-compose rm app
```

#### Clean up

```bash
docker-compose down

# Remove dangling images
docker rmi $(docker images -f "dangling=true" -q)
# Remove unused docker volumes
docker volume prune
```

## 2. 배포 리포지터리 설정
1. 아래와 같이 Cloud9 환경에서 배포 리포지터리 파일 (Helm)을 클론합니다.<br>

```bash
cd ~/environment/
git clone https://github.com/shkim4u/m2m-flightspecial-helm.git
cd m2m-flightspecial-helm
```

2. 위에서 받은 소스를 배포 리포지터리 (CodeCommit)과 연결합니다.<br>
```bash
export HELM_CODECOMMIT_URL=$(aws codecommit get-repository --repository-name M2M-FlightSpecialCICDStack-DeployStack-DeploySourceRepository --region ap-northeast-2 | grep -o '"cloneUrlHttp": "[^"]*'|grep -o '[^"]*$')
echo $HELM_CODECOMMIT_URL

# CodeCommit 배포 리포지터리(ccorigin으로 명명)와 연결
git remote add ccorigin $HELM_CODECOMMIT_URL

# 배포 리포지터리에 푸시
git push --set-upstream ccorigin main
```

## 3. 빌드 파이프라인 연동
![GitOps Pipeline](./docs/assets/gitops_helm.png)

1. 아래와 같이 Cloud9 환경에서 "m2m-flightspecial" 어플리케이션을 클론합니다.<br>

```bash
cd ~/environment
git clone https://github.com/shkim4u/m2m-flightspecial.git
cd m2m-flightspecial
```
2. 빌드 파이프라인 소스 리포지터리의 URL을 확인합니다.<br>
   ![FlightSpecial 소스 리포 URL](./docs/assets/flightspecial-codecommit-repo-url.png)

3. 위에서 확인한 리포지터리 URL과 현재 소스 코드를 연결합니다.<br>
```bash
# AWS CLI를 통해서도 HTTPS URL을 바로 확인할 수 있습니다.
export APP_CODECOMMIT_URL=$(aws codecommit get-repository --repository-name M2M-FlightSpecialCICDStack-SourceRepository --region ap-northeast-2 | grep -o '"cloneUrlHttp": "[^"]*'|grep -o '[^"]*$')
echo $APP_CODECOMMIT_URL

# CodeCommit 소스 리포지터리(ccorigin으로 명명)와 연결
git remote add ccorigin $APP_CODECOMMIT_URL
# (예시)
# git remote add origin https://git-codecommit.ap-northeast-2.amazonaws.com/v1/repos/M2M-FlightSpecialCICDStack-SourceRepository

# 소스 리포지터리에 푸시
git push --set-upstream ccorigin main
```

4. 빌드 파이프라인이 성공적으로 수행되는지 확인합니다.<br>
   ![Build Pipeline Success](./docs/assets/flightspecial-build-pipeline-success.png)


## 4. ArgoCD 설정
1. ArgoCD 접속에 필요한 정보 확인 및 접속<br>
CDK를 통해서 이미 배포한 EKS 클러스터에는 ArgCD가 설치되어 있으며, 또한 AWS ELB (Elastic Load Balancer)를 통하여 외부에서 접속할 수 있습니다.<br>

아래와 같이 접속에 필요한 URL과 ```admin``` 암호를 확인합니다.<br>

```bash
# ArgoCD 접속 주소 확인
export ARGOCD_SERVER=`kubectl get ingress/argocd-server -n argocd -o json | jq --raw-output .status.loadBalancer.ingress[0].hostname`
echo https://${ARGOCD_SERVER}

# ArgoCD의 기본 사용자 (admin) 패스워드 확인
ARGO_PWD=`kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d`
echo $ARGO_PWD
```

확인한 접속 주소와 패스워드를 사용하여 ArgoCD Web UI에 접속해 봅니다.<br>
![ArgoCD UI](./docs/assets/argocd_login.png)

2. ArgoCD가 빌드 리포지터리에 접속할 수 있도록 IAM 사용자 및 Git Credentials을 생성합니다.<br>
   i. ArgoCD 접속 IAM 사용자 생성<br>
   ![ArgoCD IAM 사용자 생성 1](./docs/assets/argocd-iam-user-step1.png)<br>
   ii. IAM 사용자 권한 지정 - CodeCommit Power User <br>
   ![ArgoCD IAM 사용자 생성 2](./docs/assets/argocd-iam-user-permission.png)<br>
   iii. IAM 사용자 생성 확인<br>
   ![ArgoCD IAM 사용자 생성 3](./docs/assets/argocd-iam-user-confirmation.png)<br>
   iv. IAM 사용자 보안 자격 증명<br>
   ![ArgoCD IAM 사용자 생성 4](./docs/assets/argocd-iam-user-security-credentials.png)<br>
   v. IAM 사용자 Git Credential 생성<br>
   ![ArgoCD IAM 사용자 생성 5](./docs/assets/argocd-iam-user-git-credentials.png)<br>
   vi. IAM 사용자 Git Credential 메모<br>
   ![ArgoCD IAM 사용자 생성 6](./docs/assets/argocd-iam-user-git-credentials-memo.png)<br>

3. ArgoCD 설정<br>
- 로그인 이후 좌측의 Settings 를 클릭한 뒤 Repositories 항목을 클릭합니다.<br>
![ArgoCD Repository Settings](./docs/assets/argo-setting.png)

- Connect Repo 버튼을 클릭하고 Method는 ```VIA HTTPS```, Project는 ```default```를 입력합니다.<br>

- Repository URL에는 앞서 확인한 배포 CodeCommit Repository의 HTTPS 주소 (예: To ```https://git-codecommit.ap-northeast-2.amazonaws.com/v1/repos/M2M-FlightSpecialCICDStack-DeployStack-DeploySourceRepository```
), Username 및 Password는 메모해 둔 정보를 입력합니다.<br>
![ArgoCD Repository Connect](./docs/assets/argocd-repository-information.png)

- Application 텝에서 NewApp버튼을 클릭합니다. Application Name 에는 ```flightspecials```를, Project는 default를 입력합니다. Sync Policy에는 "Manual"을, Repository URL에는 앞서 설정한 배포 리포지터리를, PATH에는 ```.```을 각각 입력합니다. Destination 섹션의 Cluster URL에는 ```https://kubernetes.default.svc```, Namespace에는 ```flightspecials```를 입력하고 상단의 Create를 클릭합니다.<br>
![ArgoCD FlightSpecials App](./docs/assets/argcd-app-flightspecials.png)

## 5. ArgoCD 배포 상태 확인<br>
1. ArgoCD 화면에서 FlightSpecials의 배포 상태를 확인합니다.<br>
![ArgoCD FlightSpecials App Status](./docs/assets/argocd-flightspecials-app-status.png)

2. (오류 처리 예) 오류가 발생하였으면 App을 클릭하여 들어가서 자세한 상태를 봅니다.<br>
   ![FligtSpecials Status Failed](./docs/assets/argocd-flightspecials-sync-failed.png)<br>
   ![FligtSpecials Status Failed Reasons](./docs/assets/argocd-flightspecials-app-failed-no-namespace.png)<br>
- 위에서 보듯 Namespace가 없어서 에러가 발생하였습니다.<br>
- 아래와 같이 Namespace 정의 파일을 생성한 후 배포 리포지터리에 다시 푸시하면 오류가 해소됩니다.<br>
   ![FlightSpecials Namespace](./docs/assets/flightspecials-namespace.png)<br>
   ![FlightSpecials Namespace](./docs/assets/fligtspecials-push-namespace.png)<br>

3. (오류 처리 예) 여전히 Application이 Degrade 상태에 머물러 있을 수 있습니다.<br>
![FlightSpecials Degraded](./docs/assets/fligtspecial-app-degraded.png)<br>
![FlightSpecials Degraded Reason](./docs/assets/fligtspecial-app-degraded-reason.png)<br>

위에서 보듯이 Deployment에 Templating된 컨테이너 이미지의 값이 정확하게 풀리지 않음으로써 발생하는 문제입니다.<br>

4. (오류 처리 예) 빌드 파이프라인 수행 시에 빌드되는 컨테이너 이미지를 배포 (Helm) 리포지터리에 주입해 줌으로써 이 문제를 해결하도록 해보겠습니다. 이 작업은 ```m2m-flightspecials``` 프로젝트에서 수행합니다.<br>
핵심 부분은 post_build 부분에 정의된 아래 부분입니다.<br>
   i. 배포 (Helm) 리포를 Git Clone<br>
   ii. Deployment에 주입되는 컨테이너 이미지 정보를 환경 변수로부터 치환<br>
   iii. 배포 리포에 푸시<br>
   iv. ArgoCD가 해당 컨테이너 이미지를 Picking하여 배포<br>
```yaml
...
  post_build:
     commands:
        #      - |
        #        echo "### Pushing container image tag to SSM for reuse ###"
        #        aws ssm put-parameter --name $IMAGE_TAG_KEY --value $IMAGE_TAG --type String --region $REGION --overwrite
        - echo "${IMAGE_TAG}" >> build_output.txt
        - git config --global --replace-all credential.helper '!aws codecommit credential-helper $@'
        - |
             echo "### Update value to manifest repository ###"
                #        [TODO] 이 값도 CodeBuild의 환경변수 혹은 CloudFormation Output으로부터 주입되면 좋습니다.
             git clone https://git-codecommit.ap-northeast-2.amazonaws.com/v1/repos/M2M-FlightSpecialCICDStack-DeployStack-DeploySourceRepository
             cd M2M-FlightSpecialCICDStack-DeployStack-DeploySourceRepository
             ls
             cat values-template.yaml | envsubst > ./values.yaml
             cat ./values.yaml
             git status
             git config user.email "anyone@example.com"
             git config user.name "FlightSpecial Developer"
             git add .
             git commit -m "Updated image tag to $IMAGE_TAG"
             git log --oneline
             git remote -v
             git push -u origin main
...
```

![FlightSpecial Buildspec Updated](./docs/assets/flightspecial-buildspec-reflect-deploy.png)

5. (옵션) Parameter Store/Secrets Manager 접근 권한 부족 - IRSA (IAM Role for) 설정 필요<br>
![FlightSpecials IRSA Required](./docs/assets/flightspecials-no-iam-role.png)

6. (오류 처리 예) 빌드 파이프라인을 통해 컨테이너 이미지 정보가 정확하게 Deployment 명세로 주입이되면 Pod가 기동됨을 알 수 있습니다.<br>

하지만 아래와 같이 Database 접속이 되지 않음에 따라 어플리케이션 기동이 실패하고, Pod가 재시작을 반복하면서 CrashLoopBackoff 단계에 빠진 것을 알 수 있습니다.<br>

![FlightSpecial App Pod Trying Start](./docs/assets/flightspecial-app-trying-to-start.png)<br>

![FlightSpecial App Pod in CrashLoopBack](./docs/assets/flightspecial-in-crashloopbackoff.png)<br>

데이터베이스 정보를 주입해 줌으로써 이를 해소해 보겠습니다.

7. 데이터베이스 정보 주입<br>
동적인 환경으로부터 설정값을 가져오는 방법은 여러가지가 있지만 여기에서는 다시 빌드 시 데이터베이스 엔드포인트와 포트는 CloudFormation으로부터, 그리고 데이터베이스 사용자 및 암호는 Secrets Manager로부터 얻어온 후 이 값으로 배포 Helm Chat의 values.template을 업데이트해 주기로 하였습니다.<br>
```bash
# buildspec,yaml 파일으 28 ~ 32번째 줄...
...
      - echo "### Retrieving database information..."
      - export DATABASE_ENDPOINT=`aws cloudformation describe-stacks --region ${AWS_REGION} --query "Stacks[?StackName=='M2M-FlightSpecialDatabaseStack'][].Outputs[?OutputKey=='M2MFlightSpecialDatabaseStackFlightSpecialDBEndpoint'].OutputValue" --output text`;
      - export DATABASE_PORT=`aws cloudformation describe-stacks --region ${AWS_REGION} --query "Stacks[?StackName=='M2M-FlightSpecialDatabaseStack'][].Outputs[?OutputKey=='M2MFlightSpecialDatabaseStackFlightSpecialDBPort'].OutputValue" --output text`;
#      - [TODO] DATABASE_USERNAME: Currently fixed from secrets manager.
      - export DATABASE_USERNAME=flightspecial_db_credentials_test
...
```
![FlightSpecials Database Info](./docs/assets/flightspecials-database-info.png)

이렇게 설정된 값은 다음과 같은 경로로 전파됩니다.<br>
> values.yaml -> deployment.yaml -> 컨테이너의 환경 변수 -> 어플리케이션의 applications.yaml 파일에서 Replace되어 Property Value 처리 로직에 따라 해석됨 

8. Secrets Manager 및 Parameter Store 의존성 설정<br>
아래와 같이 Secrets Manager 및 Parameter Store의 의존성을 설정해 줍니다.<br>
![FlightSpecials](./docs/assets/flightspecials-spring-cloud-aws-dependency.png)

9. (옵션) Secrets Manager 및 Parameter Store 권한 설정<br>
IRSA (IAM Role for Service Account) 혹은 Node Role에 아래와 같이 권한을 지정해 줍니다.<br>
![FlightSpecials Node Role](./docs/assets/flightspecials-node-role.png)

10. (옵션) PostgreSQL 데이터베이스 확인<br>

FlightSpecials 어플리케이션은 최초 기동 시에 데이터베이스 스키마를 설정하고 샘플 데이터를 주입하도록 설정되어 있습니다 (via Flyway). 하지만 이 과정이 실패할 경우 아래와 같이 PostgreSQL 클라이언트를 (서버에 포함) 설치하고 Troubleshooting해 볼 수 있습니다.<br> 
```bash
sudo yum update -y
sudo amazon-linux-extras enable postgresql14
sudo yum install postgresql-server -y

psql -h 서버주소 -U 아이디 데이터베이스명
# (예) psql -h travelbuddy-test-postgres-db.ceamvivkqhbk.ap-northeast-2.rds.amazonaws.com -U postgres travelbuddy
```

11. 분리된 FlightSpecials 마이크로서비스가 동작하는 것을 확인합니다.<br>

```bash
export API_URL=http://$(kubectl get ingress/travelbuddy-ingress -n travelbuddy -o jsonpath='{.status.loadBalancer.ingress[*].hostname}')
echo ${API_URL}/flightspecials/

curl ${API_URL}/flightspecials/
```

![FlightSpecials in Action](./docs/assets/flightspecials-microservice-in-action.png)


우리는 TravelBuddy 실습 중에 생성한 ALB가 공유됨을 주목할 필요가 있습니다.<br>
![TravelBuddy ALB Shared](./docs/assets/travelbuddy-ingress-shared.png)

## 6. Argo Rollouts을 통한 Canary 배포<br>
우리는 TravelBuddy Monolith 어플리케이션으로부터 FlightSpecials 서비스의 백엔드 기능을 API로 분리하고 이를 ArgoCD를 활용한 GitOps 체계로 배포할 수 있었습니다.

하지만 현재 배포된 FlightSpecials 백엔드 기능은 각 FlightSpecials 항목의 이름을 수정하는 기능이 누락된 것이 발견되었습니다.

다음과 같이 특정 FlightSpecials 항목의 헤더를 업데이트하는 REST API가 404로 반환됨을 확인합니다.

```bash
export API_URL=http://$(kubectl get ingress/travelbuddy-ingress -n travelbuddy -o jsonpath='{.status.loadBalancer.ingress[*].hostname}')
echo $API_URL/flightspecials/1/header

curl --location ${API_URL}/flightspecials/1/header \
--header 'Content-Type: application/json' \
--data '{
    "id": 1,
    "flightSpecialHeader": "London to Seoul"
}'
```

![FlightSpeicials Header Update Missing](./docs/assets/flightspecials-update-header-missing.png)


FlightSpecials 마이크로서비스의 PO (Product Owner)는 Progressive Delivery & Deploy를 적용하기 위하여 Argo Rollouts을 적용해 보고 싶어합니다. 

앞서 CDK 혹은 테라폼으로 배포한 자원에는 이미 Argo Rollouts 컨트롤러가 설치되어 있습니다.<br>
이를 활용하여 FlightSpecials에 대한 Canary 배포를 적용해 보기로 하겠습니다.<br>

1. (옵션) Argo Rollouts Dashboard 사용을 위해 Plugin 설치<br>
우리는 이미 대시보드를 설치해 두었으므로 아래 Kubectl Plugin은 옵션으로 설치합니다.<br>
 ```bash
 curl -LO https://github.com/argoproj/argo-rollouts/releases/latest/download/kubectl-argo-rollouts-linux-amd64
 chmod +x ./kubectl-argo-rollouts-linux-amd64
 sudo mv ./kubectl-argo-rollouts-linux-amd64 /usr/local/bin/kubectl-argo-rollouts
 kubectl argo rollouts version
 ```

2. Argo Rollouts 대시보드 확인<br>
```bash
# Argo Rollouts 접속 주소 확인
export ARGO_ROLLOUTS_DASHBOARD_URL=`kubectl get ingress/argo-rollouts-dashboard -n argo-rollouts -o json | jq --raw-output .status.loadBalancer.ingress[0].hostname`
echo http://${ARGO_ROLLOUTS_DASHBOARD_URL}
```

3. 위에서 확인한 ```http://<Argo Rollouts Dashboard URL>```으로 접속해 봅니다.<br>
> (참고)
> Argo Rollouts의 기본 포트는 3100이지만, CDK 혹은 테라폼 Addon으로 배포된 Ingress는 80으로 설정되었습니다.

![Argo Rollouts Dashboard](./docs/assets/argo-rollouts-dashboard.png)

4. 해당 기능이 구현된 소스를 ```main``` 브랜치에 병합합니다. 이 기능은 강사에 의해 미리 구현되어 원본 Github Repository의```feature/update-header``` 브랜치에 올라가 있습니다.

```bash
# 브랜치 전환
git switch feature/update-header

# CodeCommit 리모트 리포지터리에 해당 브랜치 푸시
git push --set-upstream ccorigin feature/update-header

# AWS CodeCommit 콘솔 화면에서 Pull Request를 생성하고 이를 ```main``` 브랜치에 병합합니다.
# 참고: https://catalog.workshops.aws/cicdonaws/ko-KR/lab02/6-create-pull-request
```

![Create Pull Request](./docs/assets/create-pull-request-01.png)


5. 배포 리포지터리의 Deployment 파일을 아래와 같이 변경하고 Rollouts 객체가 정상적으로 동작하는지 확인합니다.<br>
```bash
# 빌드 파이프라인인 배포 파일을 변경하였으므로 먼저 변경 사항을 Pull합니다.
cd ~/environment/m2m-flightspecial-helm
git pull

# Cloud9 CLI로 deployment.yaml 파일을 엽니다.
c9 open templates/deployment.yaml
```

위에서 열린 Deployment 매니페스트의 내용 전체를 아래 Argo Rollouts 매니페스트 파일로 변경합니다.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  namespace: {{ .Values.namespace.name }}
  name: {{ .Values.deployment.name }}
spec:
  replicas: {{ .Values.deployment.replicas }}
  selector:
    matchLabels:
      app.kubernetes.io/name: {{ .Values.app.name }}
  template:
    metadata:
      labels:
        app.kubernetes.io/name: {{ .Values.app.name }}
    spec:
      serviceAccountName: flightspecials-service-account
      containers:
        - image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          name: {{ .Values.app.name }}
          imagePullPolicy: Always
          ports:
            - containerPort: {{ .Values.container.port }}
              protocol: TCP
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          env:
            - name: PROFILE
              valueFrom:
                configMapKeyRef:
                  key: environment
                  name: flightspecials-configmap
            - name: JAVA_OPTIONS
              value: "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}"
            - name: SPRING_PROFILES_ACTIVE
              valueFrom:
                configMapKeyRef:
                  key: environment
                  name: flightspecials-configmap
            - name: DATABASE_ENDPOINT
              valueFrom:
                configMapKeyRef:
                  key: database_endpoint
                  name: flightspecials-configmap
            - name: DATABASE_PORT
              valueFrom:
                configMapKeyRef:
                  key: database_port
                  name: flightspecials-configmap
            - name: DATABASE_USERNAME
              valueFrom:
                configMapKeyRef:
                  key: database_username
                  name: flightspecials-configmap
            - name: DB_DEFAULT_SCHEMA
              value: travelbuddy
  strategy:
    canary:
      maxSurge: "50%"    # canary 배포로 생성할 pod의 비율*/
      maxUnavailable: 0  # 업데이트 될 때 사용할 수 없는 pod의 최대 수*/
      steps:
        - setWeight: 20
        - pause: {duration: 30s}
        - setWeight: 40
        - pause: {duration: 30s}
        - setWeight: 60
        - pause: {duration: 30s}
        - setWeight: 80
        - pause: {duration: 20s}
  revisionHistoryLimit: 2
```

Helm 배포 리포지터리에 푸시합니다.<br>
```bash
git commit -am "Argo Rollouts Applied"
git push
```

6. 어플리케이션을 신규 배포하면서 Canary 배포가 동작함을 확인합니다.<br>

```FlightSpecials``` CodeCommit 리포지터리에서 Pull Request를 생성하고 ```main``` 브랜치에 병합합니다.<br>
![Create Pull Request](./docs/assets/create-pull-request-01.png)
![Merge Pull Request](./docs/assets/merge-pull-request.png)
![Merge Pull Request 2](./docs/assets/merge-pull-request2.png)

Pull Request가 ```main``` 브랜치에 병합되면, 빌드 파이프라인이 자동으로 실행됩니다.<br>

![Pull Request Pipeline](./docs/assets/pull-request-pipeline.png)


ArgoCD 화면에서 Application을 동기화합니다.<br>
![ArgoCD Sync App](./docs/assets/argocd-sync-app.png)



![FlightSpecials in Canary Deployment](./docs/assets/argo-rollouts-flightspecials-steps.png)
![FlightSpecials in Canary Deployment](./docs/assets/argo-rollouts-flightspecials.png)


7. FlightSpecials 헤더 업데이트 기능이 정상 작동하는지 확인합니다.<br>

> (참고)<br>
> Argo Rollouts에 의해 신규 마이크로서비스가 배포된 후에도 여전히 기존의 마이크로서비스 Pod가 Running 중일 수 있습니다.<br>
> 이는 Argo Rollouts 매니페스트에 ```revisionHistoryLimit: 2```로 설정되어 있기 때문입니다.<br>
> 아래와 같이 Application을 동기화할 때 ```Prune``` 옵션을 선택하고 동기화하면 Git 형상에 존재하지 않는 Deployment를 삭제하면서 Argo Rollouts으로 전환할 수 있습니다.

![ArgoCD Synd App with Prune](./docs/assets/argocd-sync-app-with-prune.png)

```bash
export API_URL=http://$(kubectl get ingress/travelbuddy-ingress -n travelbuddy -o jsonpath='{.status.loadBalancer.ingress[*].hostname}')
echo $API_URL/flightspecials/1/header

curl --location ${API_URL}/flightspecials/1/header \
--header 'Content-Type: application/json' \
--data '{
    "id": 1,
    "flightSpecialHeader": "London to Seoul"
}'
```
![Successful FlightSpecials Update Header](./docs/assets/successful-update-header.png)




## 7. 데이터 동기화<br>

> (참고)<br>
> AWS DMS를 사용한 데이터베이스 복제 워크샵: https://catalog.us-east-1.prod.workshops.aws/workshops/77bdff4f-2d9e-4d68-99ba-248ea95b3aca/en-US/oracle-oracle/data-migration/replication-instance


우리는 FlightSpecials 마이크로서비스를 모놀리스로부터 분리한 후 성공적으로 동작하는 것을 확인하였습니다. 그리고 해당 마이크로서비스의 배포에 점진적 전달을 적용하기 위하여 Argo Rollouts을 적용하였습니다.

하지만 마이크로서비스 전환은 대개의 경우 기존의 모놀리스 기능과 병행하면서 점진적으로 이루지므로, 마이크로서비스에서 생성되는 데이터가 기존의 모놀리스 환경으로 동기화되어야 할 수도 있습니다.

### 7.1. DMS 복제 인스턴스 생성<br>
이름은 ```m2m-flightspecials-sync-dms```로 지정합니다.<br>
(참고) 아래에서 보안 그룹은 "default"로 지정되고 있는데 실습 자원과 함께 생성된 보안 그룹 (예: Database-Replicaton-Instance-SecurityGroup 이름 포함한 보안 그룹) 

![Create DMS Replication Instance 01](./docs/assets/create-dms-01.png)<br>
![Create DMS Replication Instance 02](./docs/assets/create-sync-dms-02-configuration.png)<br>
![Create DMS Replication Instance 03](./docs/assets/create-sync-dms-03-storage-network.png)<br>
![Create DMS Replication Instance 04](./docs/assets/create-sync-dms-04-creating.png)<br>


> (참고)<br>
> ```The IAM Role arn:aws:iam::957465301138:role/dms-vpc-role is not configured properly.```와 같은 오류 메시지가 표시되는 경우 ```취소```를 클릭하고 위의 단계를 반복하면 됩니다.

### 7.2. DMS 소스 엔드포인트 생성<br>

1. 소스 엔드포인트 생성
AWS DMS 콘솔 화면의 왼쪽 ```엔드포인트``` 링크를 클릭한 다음 오른쪽 상단의 ```엔드포인트 생성``` 버튼을 클릭합니다.
![Create DMS Source Endpoint](./docs/assets/create-dms-source-endpoint.png)

2. 다음과 같이 정보를 입력합니다.<br>

![Create DMS Source Endpoint 01](./docs/assets/create-dms-source-endpoint-01.png)<br>
![Create DMS Source Endpoint 02](./docs/assets/create-dms-source-endpoint-02.png)<br>

[//]: # (| 보안 암호 ID                         | ```aws secretsmanager list-secrets --output json \| jq --raw-output .SecretList[0].ARN``` 을 실행하여 표시되는 값 |)
[//]: # (aws secretsmanager get-secret-value --secret-id `aws secretsmanager list-secrets --output json | jq --raw-output .SecretList[0].ARN`)

| **파라미터**                         | **값**                                                                                                                                                                                           |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 엔드포인트                            | 소스 엔드포인트                                                                                                                                                                                        |
| RDS DB 인스턴스 선택                   | 선택                                                                                                                                                                                              |
| RDS 인스턴스                         | flightspecials-test-postgres-db                                                                                                                                                                 |
| 엔드포인트 식별자                        | <자동으로 채워지는 정보 사용>                                                                                                                                                                               |
| 알기 쉬운 Amazon 리소스 이름(ARN) - 선택 사항 | <비워 둠>                                                                                                                                                                                          |
| 소스 엔진                            | PostgreSQL                                                                                                                                                                                      |
| 엔드포인트 데이터베이스에 액세스                | 수동으로 액세스 정보 제공                                                                                                                                                                                  |
| 서버 이름                            | <자동으로 채워지는 정보 선택>                                                                                                                                                                               |
| 포트                               | 5432                                                                                                                                                                                            |
| 사용자 이름                           | postgres                                                                                                                                                                                        |
| 암호                               | ```aws secretsmanager get-secret-value --secret-id `aws secretsmanager list-secrets --output json \| jq --raw-output .SecretList[0].ARN``` 로 표시되는 데이터 중 "SecretString"의 "password" 값 (강사 안내 참고) |
| Secure Socket Layer(SSL) 모드      | 없음                                                                                                                                                                                              |
| 데이터베이스 이름                        | dso                                                                                                                                                                                             |

```엔드포인트 생성``` 버튼을 눌러 엔드포인트를 생성합니다.

엔드포인트 생성 후 다음과 같이 연결 검사에서 실패할 수 있는데, 이 경우에는 Security Group을 먼저 확인해 봅니다.<br>
![Create DMS Source Endpoint - Testing Connectivity](./docs/assets/create-dms-source-endpoint-testing-connectivity.png)<br>

모든 연결이 정상적으로 테스트되면 아래와 같이 화면이 표시됩니다.<br>
![Create DMS Source Endpoint Source Test Successful](./docs/assets/create-dms-source-endpoint-test-successful.png)<br>


### 7.3. DMS 타겟 엔드포인트 생성<br>

위의 7.2. DMS 소스 엔트포인트 생성과 동일한 방법으로 타겟 엔드포인트를 생성합니다.

1. 타겟 엔드포인트 생성
   AWS DMS 콘솔 화면의 왼쪽 ```엔드포인트``` 링크를 클릭한 다음 오른쪽 상단의 ```엔드포인트 생성``` 버튼을 클릭합니다.
   ![Create DMS Source Endpoint](./docs/assets/create-dms-target-endpoint.png)

2. 다음과 같이 정보를 입력합니다.<br>

![Create DMS Target Endpoint 01](./docs/assets/create-dms-target-endpoint-01.png)<br>
![Create DMS Target Endpoint 02](./docs/assets/create-dms-target-endpoint-02.png)<br>


[//]: # (| 보안 암호 ID                         | ```aws secretsmanager list-secrets --output json \| jq --raw-output .SecretList[0].ARN``` 을 실행하여 표시되는 값 |)
[//]: # (aws secretsmanager get-secret-value --secret-id `aws secretsmanager list-secrets --output json | jq --raw-output .SecretList[0].ARN`)

| **파라미터**                         | **값**                                             |
|----------------------------------|---------------------------------------------------|
| 엔드포인트                            | 타겟 엔드포인트                                          |
| RDS DB 인스턴스 선택                   | 선택                                                |
| RDS 인스턴스                         | m2m-rdslegacystack-dbinstance-xxx 형식의 RDS 인스턴스 선택 |
| 엔드포인트 식별자                        | <자동으로 채워지는 정보 사용>                                 |
| 알기 쉬운 Amazon 리소스 이름(ARN) - 선택 사항 | <비워 둠>                                            |
| 소스 엔진                            | MySQL                                             |
| 엔드포인트 데이터베이스에 액세스                | 수동으로 액세스 정보 제공                                    |
| 서버 이름                            | <자동으로 채워지는 정보 선택>                                 |
| 포트                               | 3306                                              |
| 사용자 이름                           | root                                              |
| 암호                               | ```labpassword```                                       |
| Secure Socket Layer(SSL) 모드      | 없음                                                |
| 데이터베이스 이름                        | dso                                               |

```엔드포인트 생성``` 버튼을 눌러 엔드포인트를 생성합니다.

엔드포인트 생성 후 다음과 같이 연결 검사에서 실패할 수 있는데, 이 경우에는 역시 마찬가지로 Security Group을 먼저 확인해 봅니다.<br>

모든 연결이 정상적으로 테스트되면 아래와 같이 화면이 표시됩니다.<br>
![Create DMS Target Endpoint Source Test Successful](./docs/assets/create-dms-target-endpoint-test-successful.png)<br>

### 7.4. DMS 마이그레이션 태스크 생성

