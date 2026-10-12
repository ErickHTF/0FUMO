# 0FUMO – Smoking Cessation Support Platform

> This project is part of the **Programming Language 1** course, focused on applying requirements analysis, modeling and software development concepts to real-world problems.

## The Problem

Many people struggle to quit smoking, mainly because of physical dependence and the emotional factors involved, such as stress, anxiety and daily habits. On top of that, the lack of continuous follow-up and practical tools makes it hard to identify triggers and increases the chance of relapse. Another common problem is low adherence to existing solutions, which are often complex or hard to fit into the user's daily life.

## Solution

In this context, there is a need for **tools that can help people through the process of quitting smoking, offering follow-up, motivation and support along the way.**

## System Goals

The goal of the system is to provide a digital platform that helps people quit smoking, offering progress tracking, motivation and behavioral support tools.
The system will:

- Track the user's smoke-free progress
- Show health statistics based on time without smoking
- Log cravings to identify patterns
- Offer relaxation resources to manage anxiety
- Help the user decide to quit through an initial assessment

This way, the system aims to increase the chances of successfully quitting smoking.

## Expected Benefits

- Higher chances of successfully quitting smoking
- Better quality of life and health for users
- Lower costs associated with treating tobacco-related diseases
- Fewer relapses

## Target Audience

- People who want to quit smoking
- Healthcare professionals looking for support tools

## Repository Structure

| Folder | Contents |
|---|---|
| [`backend/`](backend) | REST API in Spring Boot 4 (Java 21, JWT, PostgreSQL) |
| [`frontend/`](frontend) | Web interface in plain HTML/CSS/JS |
| [`docs/`](docs) | Requirements, use cases, analysis models, test script, guides and project management (Word, Excel and Astah files, in Portuguese) |

## Getting Started

With Docker, this brings up the database, API and frontend at once:

```bash
docker compose --profile app up -d --build
```

- Frontend: http://localhost:3000
- API: http://localhost:8080/api

For development, start only the database and run the API and frontend locally. The step-by-step is in the [Deployment Guide](DEPLOY.md).
