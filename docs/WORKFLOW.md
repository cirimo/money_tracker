# Workflow

How a working session on this project runs. The owner clears the context between sessions, so
each one starts cold and must leave the project in a state the next one can pick up from the
written record alone.

## Starting

1. Read [STATUS.md](STATUS.md) before anything else. It is the handoff from the previous session
   and tells you the current state, what was just done, what is next and what is still open.
2. Check that the tree matches what STATUS says: `git status` and `git log --oneline -10`. If
   they disagree, trust git, tell the owner, and fix STATUS.
3. Read the documents your task touches. The map in [CLAUDE.md](../CLAUDE.md) says which.
4. If the task is unclear, or would require a decision that belongs to the owner, ask before
   starting. One good question at the start is cheaper than a wrong afternoon.

## Planning

For anything larger than a small, obvious change, write a short plan and show it to the owner
before editing: what you will change, in what order, how you will verify it, and what you are
unsure about. Plan when the work touches more than a handful of files, adds a dependency,
changes the build, or involves a choice between real alternatives. The plan exists so the owner
can redirect you before the work is done rather than after.

Do not plan small fixes. Just make them.

Stay inside the task. If you notice something else worth doing, note it in STATUS under open
items instead of doing it, unless it blocks you. Unrequested changes are hard to review and
tend to hide the change that was asked for.

## Working

Make the change in small steps and commit each one once it works. A commit should be one logical
change that leaves the build passing, so that any commit can be reverted on its own and the log
reads as a history of intent.

Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<optional scope>): <summary in the imperative, lower case, no full stop>

<optional body: what and why, wrapped at about 72 columns>
```

Types used here: `feat`, `fix`, `refactor`, `perf`, `test`, `docs`, `build`, `ci`, `chore`,
`style`. Use `feat` and `fix` only for changes a user of the app could notice.

## Verifying

Do not report anything as working unless you ran it and saw it pass. This is the rule most
worth being strict about, because the owner cannot see your terminal and will act on what you
say.

- Run `.\gradlew.bat check` before every commit that touches code or build files.
- If you changed anything the user can see or touch, build and install it and look. Run the
  instrumented tests on a device when UI behaviour changed.
- If you could not verify something (no phone attached, for example), say exactly that, and
  give the command the owner can run to verify it.
- Report failures as plainly as successes, including ones you worked around.

When a check fails, fix the cause. Do not disable the check, add a blanket suppression, lower a
threshold or create a baseline to make it pass. If a rule is truly wrong for this project,
that is a change to the rule, made on purpose with the owner's agreement and written down. If
the build or a check still fails after three distinct attempts at a fix, stop and show the owner
the error and what you tried.

## Definition of done

A piece of work is done when all of these hold:

- It does what was asked, and you have seen it do so.
- `.\gradlew.bat check` passes.
- New logic has unit tests, and a bug fix has a test that would have caught the bug.
- Every user-facing string is in resources in both English and Croatian.
- It meets the accessibility baseline in [CONVENTIONS.md](CONVENTIONS.md#accessibility-baseline).
- For anything the user sees: it follows [DESIGN.md](DESIGN.md) and passes the tests in
  [PRODUCT.md](PRODUCT.md#the-principle-fun-and-not-like-other-finance-apps).
- Documents that the change made untrue have been corrected, and any real decision has a record
  in [decisions/](decisions/README.md).
- It is committed, and [STATUS.md](STATUS.md) is up to date.

## Ending

1. Run the full check once more.
2. Commit what is finished. If something is unfinished, either leave it uncommitted and say so
   in STATUS, or commit it only if the build still passes.
3. Rewrite [STATUS.md](STATUS.md). Do not append to it; it describes the present, and git holds
   the history. Follow the structure already in the file.
4. Give the owner a short summary in Croatian: what changed, what you verified and how, what
   you did not verify, and what you need from them.
5. Ask whether to push.

## Git rules

- The default branch is `main` and the remote is `origin` on GitHub.
- You may commit without asking.
- Ask before every push and wait for an explicit yes. A yes covers that one push, not later
  ones. The owner wants to see what leaves the machine, and a push to a public repository
  cannot be fully taken back.
- Never force-push, and never rewrite history that has been pushed (no amend, rebase or reset
  of pushed commits). Fix mistakes with a new commit.
- Before every commit, look at what is staged. Secrets, keystores, `local.properties` and build
  output must never be committed. The `.gitignore` covers the known cases, but check anyway.
- Do not skip hooks or signing.
- CI runs on every push and pull request. If it fails after a push, fixing it is the next task.

## What needs the owner's yes

Stop and ask before doing any of these:

- Pushing.
- Adding a dependency or changing a tool or library version.
- Installing software on the machine or changing environment variables.
- Deleting or overwriting anything you did not create in the current session.
- Changing the application id, the minimum SDK, or anything else recorded as a decision.
- Changing what [ARCHITECTURE.md](ARCHITECTURE.md) or [DESIGN.md](DESIGN.md) decides, or
  deciding something they leave open. For design, show the owner the alternatives drawn; they
  judge by looking.
- Anything that would be visible outside this machine: publishing, uploading, creating
  releases or issues.

## Recording decisions

When a choice is made that a future session might otherwise question or undo, write a decision
record. The format and the index are in [decisions/README.md](decisions/README.md). Keep it
short: what was decided, why, and what it rules out. Records are not edited once accepted; a
change of mind is a new record that supersedes the old one.

## Notes for working on this machine

- Windows keeps files in `build/` and `.gradle/` locked while a Gradle daemon uses them. If a
  build fails on a locked file, run `.\gradlew.bat --stop` and try again.
- After moving or renaming a resource directory, the incremental resource merge can keep stale
  results and report a resource as missing. `.\gradlew.bat clean` or `--rerun-tasks` clears it.
- Line endings are normalised to LF by [.gitattributes](../.gitattributes). If git reports a
  file as changed that you did not touch, it is almost always a line ending, and
  `git add --renormalize .` settles it.
