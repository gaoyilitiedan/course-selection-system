# ==========================================
# 多阶段 Docker 构建
# Stage 1: 构建 Vue 前端
# Stage 2: 构建 Spring Boot 后端
# Stage 3: 运行时镜像
# ==========================================

# ---- Stage 1: 前端构建 ----
FROM node:20-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---- Stage 2: 后端构建 ----
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app
COPY pom.xml ./
COPY src/ src/
# 将前端构建产物放入 Spring Boot 静态资源目录
COPY --from=frontend /app/frontend/dist/ src/main/resources/static/
RUN mvn package -DskipTests -B -q

# ---- Stage 3: 运行时 ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 创建数据目录（H2 文件数据库存放位置）
RUN mkdir -p /app/data
# 复制 JAR
COPY --from=backend /app/target/*.jar app.jar
# 暴露端口
EXPOSE 3000
# 启动（激活 prod 配置）
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
