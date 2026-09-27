# mini-bank-platform

A tiny online bank built as a 2-week learning project to understand how modern
backend systems are developed, shipped and run.

## Services

| Service         | Port | What it does                                                   |
|-----------------|------|----------------------------------------------------------------|
| user-service    | 8081 | Create user, get user                                          |
| account-service | 8082 | Open account, deposit, transfer, balance; saves receipt to S3  |

## Tech stack

Java 21 · Spring Boot 3 · Maven · PostgreSQL · Swagger UI · Docker Compose ·
Kubernetes (kind + Kustomize: SIT / UAT / PROD) · k9s · GitHub Actions → AWS ECR ·
AWS S3 via AWS SDK v2 (LocalStack locally)

## Repo structure

```
services/           # the two Spring Boot microservices
infra/              # docker-compose, kind cluster, Kubernetes manifests
  k8s/base/         # shared Kubernetes config
  k8s/overlays/     # per-environment changes: sit, uat, prod
.github/workflows/  # CI/CD pipeline
docs/learning-notes # what I built, what broke, how I fixed it
```

## Branching

- Work on `feature/<name>` → pull request into `develop` → merge `develop` into `main` for a release.
- Commit messages: `feat:`, `fix:`, `docs:`, `ci:`.

## Progress

- [ ] Day 1 — Tools installed, repo on GitHub
- [ ] Days 2–3 — user-service and account-service
- [ ] Day 4 — Receipt to S3 (LocalStack)
- [ ] Day 5 — Docker Compose
- [ ] Days 6–7 — Kubernetes on kind, SIT/UAT/PROD
- [ ] Day 8 — k9s troubleshooting
- [ ] Day 9 — GitHub Actions to ECR
- [ ] Day 10 — README and demo
