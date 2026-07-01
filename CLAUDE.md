# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

学生选课系统 (Student Course Selection System) — a university course enrollment platform with role-based access (System Admin, Department Admin, Teacher, Student). Spring Boot REST API backend + Vue 3 SPA frontend.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.4.1, Spring Data JPA |
| Database | H2 (file-based: `./data/coursedb`) |
| Build | Maven (`pom.xml`) |
| Auth | Self-implemented `TokenStore` (in-memory `ConcurrentHashMap`), no Spring Security |
| Charts | JFreeChart 1.5.4 |
| Frontend | Vue 3 (Composition API), Vite, Element Plus 2, Pinia, Vue Router 4, ECharts 5, Axios |
| Testing | JUnit 5 + Spring Boot Test |

## Build & Run Commands

```bash
# Backend
./mvnw spring-boot:run          # Start backend (port 8080)
./mvnw test                      # Run all tests
./mvnw test -Dtest=ClassName     # Run a single test class
./mvnw clean package             # Build JAR

# Frontend (cd frontend/)
npm install                      # Install dependencies
npm run dev                      # Start dev server (port 5173, proxies /api → localhost:8080)
npm run build                    # Production build to dist/
```

H2 Console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/coursedb`, user: `sa`, no password).

## Architecture

**Package:** `cn.edu.ncut.cs.springboot.student`

### Backend Layers (strict 3-tier, enforced by UML architecture constraints)

```
Controller (REST, @RestController)
  └─→ Service (@Service, business logic)
        └─→ Repository (Spring Data JPA)
```

- **Never** call Repository directly from Controller (sole exception: `AuthController` has no corresponding Service)
- **Never** put business logic in Controllers
- Each Service corresponds to one Controller role; `StatisticsService` is shared across multiple Controllers

### Key Source Files

| Directory | Purpose |
|-----------|---------|
| `entity/` | JPA entities: `User` (base, JOINED inheritance), `Student`, `Teacher`, `Department`, `Course`, `Enrollment`, `Message`, `Courseware` |
| `controller/` | `AuthController`, `AdminController`, `DeptAdminController`, `TeacherController`, `StudentController` |
| `service/` | `AdminService`, `DeptAdminService`, `TeacherService`, `StudentService`, `StatisticsService` |
| `repository/` | Spring Data JPA repositories per entity |
| `util/TokenStore.java` | In-memory token management (`ConcurrentHashMap<String, User>`) |
| `config/WebConfig.java` | CORS config for `/api/**` |
| `exception/` | `BusinessException` (thrown by services) and `GlobalExceptionHandler` (`@ControllerAdvice`) |
| `CourseApplication.java` | Main class + `CommandLineRunner` seed data initializer |

### Frontend (Vue 3 SPA)

```
frontend/src/
├── api/           # Axios API modules per role (auth.js, admin.js, dept.js, teacher.js, student.js)
├── router/        # Vue Router with role-based auth guard (beforeEach checks token + meta.role)
├── stores/        # Pinia: user.js (auth + localStorage persistence), app.js (sidebar state)
├── utils/         # request.js (Axios instance + interceptors), download.js (blob downloads), constants.js (menus/roles)
├── components/    # AppLayout.vue (sidebar+topbar+main), SidebarMenu.vue, Topbar.vue
├── views/         # Pages by role: login/, admin/, dept/, teacher/, student/
│   ├── Login.vue
│   ├── admin/     # 6 pages: Chart, CRUD (Students/Teachers/Courses/Enrollments), Grades
│   ├── dept/      # 6 pages: Dept-scoped CRUD + Draw (lottery)
│   ├── teacher/   # 7 pages: Courses, Courseware, Prerequisite, SelectedStudents, Grades, Stats, Messages
│   └── student/   # 7 pages: Courses (browse+enroll), MyCourses, Info, Grades, Courseware, Messages, DrawResult
```

**Key patterns:**
- Axios response interceptor unwraps `{code, message, data}` → callers receive only `data`; non-200 codes show `ElMessage.error` and reject
- 401 auto-redirects to `/login`; 403 redirects to `/403`
- Vite dev server proxies `/api` → `localhost:8080` (no CORS issues)
- Blob downloads (chart PNG, courseware) bypass Axios, use `fetch()` with auth header in `utils/download.js`
- Menu items rendered from `MENU_CONFIG` in `constants.js`, filtered by `userStore.userInfo.role`

### Seed Data

`CourseApplication.initData()` creates sample data on startup (idempotent — checks count before inserting):

- 2 Departments, 1 System Admin (`admin/admin123`), 2 Dept Admins (`csadmin/csadmin123`, `mathadmin/mathadmin123`)
- 2 Teachers (`teacher1/123456`, `teacher2/123456`), 3 Students (`student1/123456`, `student2/123456`, `student3/123456`)
- 3 Courses (Java程序设计基础, Java Web开发 [capacity=2, prerequisite=Java基础], 高等数学)
- 3 Enrollments (Java基础 completed with grades, Java Web dev + 高等数学 pending draw)

Test accounts and cURL examples are documented in `TEST_DATA.md`.

### API Design

- **Response format:** Unified `{code, message, data}` via `GlobalExceptionHandler`
- **Auth:** `POST /api/auth/login` (public). All other endpoints require `Authorization: Bearer <token>`
- **Error handling:** Services throw `BusinessException(code, message)` → caught by `@ControllerAdvice` → returns `{code: 400/500, message, data: null}`
- **API prefixes by role:** `/api/admin/**`, `/api/dept/**`, `/api/teacher/**`, `/api/student/**`
- Full API reference: `API.md`

### Key Business Rules

- **Prerequisite check** (`StudentService.enroll`): Student must have a `SELECTED` enrollment in the prerequisite course with score ≥ 60
- **Draw algorithm** (`DeptAdminService.draw`): If PENDING count ≤ capacity → all SELECTED; else `Collections.shuffle()` and pick `capacity` winners, rest REJECTED
- **Enrollment states:** `PENDING` → (draw) → `SELECTED`/`REJECTED` → (grade entry) → terminal
- **Department isolation:** Dept Admin and Teacher operations are scoped to their own department
- **Password storage:** Plain text (simplified for educational purposes — do NOT change)
- **No Spring Security:** Auth is manual — each controller method extracts the Bearer token, looks up the user via `TokenStore`, and checks the role
- **README.md** is the architecture constraint document referencing 8 UML diagrams — class names, method signatures, entity relationships, and algorithms must follow it

### Package Name Mismatch

The README constraint document references package `com.course`, but the actual codebase uses `cn.edu.ncut.cs.springboot.student`. When implementing, use the **actual package** (`cn.edu.ncut.cs.springboot.student`).
