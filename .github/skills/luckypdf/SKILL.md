---
description: "Create a new workspace skill accessible via a slash command."
user-invocable: true
---

# Slash Command Skill

Use this skill when you want to add a new repository-owned workspace skill that is visible as a chat slash command.

## What this skill does

- Collects the desired outcome for the new skill in one sentence
- Chooses a workspace-level location under `.github/skills/`
- Generates a valid `SKILL.md` file with YAML frontmatter
- Ensures the generated skill is easy to maintain and can be reused by the team

## When to use

- You want a workspace skill that appears as `/slash-command` in chat
- You need to make a new repository skill available to all contributors
- You want to encapsulate a repeated workflow as a reusable command

## How to use

1. Describe the new skill goal in one sentence.
2. Specify the target audience or task for the skill.
3. Provide any constraints, conventions, or best practices.
4. Run the skill and review the resulting file under `.github/skills/`.

## Validation

- The new skill file must live in `.github/skills/<skill-name>/SKILL.md`
- The frontmatter must include `name`, `description`, and `user-invocable`
- The instruction body must be concise, actionable, and workspace-scoped
- The skill should not depend on personal user settings or external secrets
