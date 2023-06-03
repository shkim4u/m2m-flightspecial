# Docker compose

## Agenda

- 소개
- 설치 방법
- 활용 전략

## 소개

도커 컴포즈는 여러 개의 도커 컨테이너를 정의하고 실행하기 위한 도구입니다. 하나의 YAML 파일에 여러 개의 서비스 정의를 작성하고 이를 실행하는 것으로 간단하게 여러 개의 컨테이너를 띄울 수 있습니다. 이러한 서비스 정의에는 컨테이너의 이미지, 포트, 환경변수, 볼륨 마운트 등을 설정할 수 있습니다.

도커 컴포즈는 개발자나 시스템 관리자들이 로컬 개발 환경이나 실제 운영 환경에서 여러 개의 컨테이너를 쉽게 관리할 수 있도록 도와줍니다. 또한, 여러 개의 컨테이너를 하나의 애플리케이션으로 구성할 수 있으며, 여러 개의 컨테이너를 쉽게 스케일링할 수 있습니다.

## 설치 방법 (Cloud9 - Amazon Linux 2 기준)

패키지 관리자를 업데이트합니다.

```bash
sudo yum update
```

Docker 및 docker-compose를 설치합니다.

```bash
# docker 설치 (docker를 미리 설치한 경우 생략합니다)
sudo yum install docker
sudo service docker start
sudo usermod -a -G docker ec2-user
sudo chkconfig docker on

# docker-compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

## 활용 1. 마이크로서비스용 DB

```yaml
version: "3.7"

services:
  db:
    image: postgresql:latest
    ports:
      - "${DB_PORT:-5432}:5432"
  service-a:
    depends_on:
      - db
    image: service-b:latest
    ports:
      - "9001:8080"
    environment:
      - DB_CONN=jdbc:postgresql://db:${DB_PORT:-5432}/${DB_NAME}
  service-c:
    depends_on:
      - db
    image: service-c:latest
    ports:
      - "9002:8080"
    environment:
      - DB_CONN=jdbc:postgresql://db:${DB_PORT:-5432}/${DB_NAME}
```

환경변수로 DB_PORT를 지정하면 해당 포트를 사용하고, 지정하지 않으면 기본 포트인 5432를 사용합니다.

서비스 A와 서비스 B는 모두 db에 의존하므로 db가 실행된 이후에 실행됩니다.

## 활용 2. 외부 volume 또는 파일 시스템에 DB 유지

```yaml
version: "3"

services:
  app:
    image: myapp
    environment:
      - DB_CONN=jdbc:postgresql://db:${DB_PORT:-5432}/${DB_NAME}
  db:
    image: postgresql:latest
    volumes:
      - ${DB_VOLUME:-./db/data}:/var/lib/postgresql/data
volumes:
  myapp-data:
    external: true
```

`external` 옵션을 `true`로 설정하면 외부에서 생성된 볼륨을 사용합니다.

외부 볼륨을 사용하는 경우 DB_VOLUME 변수에 해당 이름을 정의하거나 DB_VOLUME을 정의하지 않으면 `./db/data` 폴더를 사용합니다.

## 활용 3. 마이크로서비스 애플리케이션 실행

```yaml
version: "3.7"

services:
  service-a:
    image: service-a:latest
    ports:
      - "9000:8080"
    environment:
      - SERVICE_B_URL=http://service-b:9001
      - SERVICE_C_URL=http://service-c:9002
  service-b:
    image: service-b:latest
    ports:
      - "9001:8080"
    environment:
      - SERVICE_C_URL=http://service-c:9002
  service-c:
    image: service-c:latest
    ports:
      - "9002:8080"
```

- 각 서비스 다른 서비스를 호출하기 위해서 환경변수로 주소를 전달 받습니다.
  - 서비스 A는 서비스 B, C에 의존
  - 서비스 B는 서비스 C에 의존
- 각 서비스에서 다른 서비스를 호출할 때는 서비스 이름으로 호출
  - 서비스 A에서 서비스 B를 호출할 때, `http://service-b:9001`
  - 서비스 B에서 서비스 C를 호출할 때, `http://service-c:9002`

## APPENDIX. 활용 예시

워드프레스를 실행하는 예시입니다.

```yaml
version: "3.7"

services:
  db:
    image: mysql:5.7
    volumes:
      - db_data:/var/lib/mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: wordpress
      MYSQL_DATABASE: wordpress
      MYSQL_USER: wordpress
      MYSQL_PASSWORD: wordpress

  wordpress:
    depends_on:
      - db
    image: wordpress:latest
    ports:
      - "8000:80"
    restart: always
    environment:
      WORDPRESS_DB_HOST: db:3306
      WORDPRESS_DB_USER: wordpress
      WORDPRESS_DB_PASSWORD: wordpress
      WORDPRESS_DB_NAME: wordpress
volumes:
  db_data:
```

위 예시에서는 mysql 데이터를 db_data 이름의 볼륨에 저장하여 영속적으로 보관합니다.
