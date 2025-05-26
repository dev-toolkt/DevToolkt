# KotlinMultiplatformJvmJsTemplate

## Recommended repository setup

### [Settings](https://github.com/cubuspl42/KotlinMultiplatformJvmJsTemplate/settings)

#### Pull Requests

- Allow merge commits: **No** (default: **Yes**)
- Allow squash merging: **No** (default: **Yes**)
- Allow rebase merging: **Yes** (default)
- Allow auto-merge: **Yes** (default: **No**)
- Automatically delete head branches: **Yes** (default: **No**)

### [Rulesets](https://github.com/cubuspl42/KotlinMultiplatformJvmJsTemplate/settings/rules)

#### Main branch ruleset

- Ruleset Name: `main`
- Enforcement status: **Active** (default: **Disabled**)
- Bypass list: (empty)
- Target branches:
  - **Default** (_Include default branch_)

##### Rules
 
###### Branch rules

- Require linear history: **Yes** (default: **No**)
- Require a pull request before merging: **Yes** (default: **No**)
- Require status checks to pass: **Yes**
  - Status checks that are required:
    - `test`
- Allowed merge methods: **Rebase** (default: **Rebase, Merge, Squash**)
