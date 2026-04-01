
[![Actions Status](https://github.com/Gushchin-A/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Gushchin-A/java-project-72/actions)
[![CI build and sonar](https://github.com/Gushchin-A/java-project-72/actions/workflows/CI.yml/badge.svg)](https://github.com/Gushchin-A/java-project-72/actions/workflows/CI.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Gushchin-A_java-project-72&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Gushchin-A_java-project-72)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Gushchin-A_java-project-72&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Gushchin-A_java-project-72)

## Page Analyzer

Page Analyzer is a web application built with **Javalin** for checking websites and storing page analysis results.  
It uses routing, server-side templates, Bootstrap, and JDBC-based database access.

#### Database
- **H2** — for local development and tests
- **PostgreSQL** — for production

#### Deployment
- **Render**

#### Website
https://java-project-72-5s47.onrender.com/

---

### Project launch
```bash
make build    # build the project
make run      # start the application
make test     # run tests