---
name: create-skill
description: "Guide the user to create a new workspace SKILL.md file for repository-specific automation or workflow support."
user-invocable: true
---

# Create a Workspace Skill

Use this skill when you want to define a new repository-owned skill for LuckyPDF.

## What this skill does

- Asks for the intended outcome of the skill
- Chooses a workspace-level location under `.github/skills/`
- Generates a valid `SKILL.md` file with YAML frontmatter and concise workflow instructions
- Verifies that the file contents are easy to maintain and reuse

## When to use this skill

- You want to package a multi-step workflow into a reusable repository skill
- You need a skill that is shared by the LuckyPDF team
- You want to create a skill that can be invoked directly from chat

## How to use

1. Describe the goal of the skill in one sentence.
2. Identify the target audience or task (e.g. code review, bug fix, build verification, feature scaffolding).
3. Provide any relevant constraints or best practices for the workflow.
4. Run the skill and review the generated `SKILL.md` file.

## Example prompts

- "Create a skill that helps me scaffold a new Jetpack Compose screen with MVVM and state handling."
- "Create a skill to generate Kotlin unit test templates for ViewModel logic."
- "Create a skill to summarize repository architecture and file responsibilities."

## Validation

- The file must be placed in `.github/skills/<skill-name>/SKILL.md`
- The frontmatter must include `name`, `description`, and `user-invocable`
- The instruction body must be clear, concise, and actionable
- The skill should be workspace-scoped and not depend on personal settings
