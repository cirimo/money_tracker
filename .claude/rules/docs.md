---
paths:
  - "CLAUDE.md"
  - "docs/**"
  - ".claude/rules/**"
---

# Editing the instruction files

These files are the only memory a future session has. It will read them with no other context
and act on them literally, so an error here is repeated by every session until someone notices.

- Write for a capable colleague who has never seen the project: plain prose, specific, in
  English. Give the reason behind every rule, so the reader can apply it to cases the rule does
  not mention. Avoid capitalised commands and long lists of prohibitions; they get skimmed.
- State each fact in exactly one place and link to it from elsewhere. Before adding something,
  search for where it already lives. Two copies drift apart, and the reader cannot tell which
  one is current.
- Keep `CLAUDE.md` to roughly 150 lines. It is loaded into every session, so every line costs
  attention. It holds what every session needs; details go in `docs/`, and guidance that only
  matters for certain files goes in a path-scoped rule here in `.claude/rules/`.
- Describe the project as it is. Remove or correct what has become untrue, rather than adding
  a note beside it. Do not write about what a past session did, except in `docs/STATUS.md`.
- `docs/STATUS.md` is rewritten at the end of every session, keeping its sections. It must
  distinguish what was verified by running it from what was not.
- Decision records in `docs/decisions/` are not edited after they are accepted, apart from
  their status. A changed decision gets a new record; see `docs/decisions/README.md`. Add new
  records to the index there.
- `docs/DESIGN.md` is a stub to be replaced whole by its dedicated session. Outside that
  session, only add to its list of open decisions.
- `docs/ARCHITECTURE.md` is written. Correct it when the code makes it untrue, but a change to
  what it decides needs the owner's agreement and a new decision record.
- Use relative Markdown links between files, and check after renaming a heading or a file that
  the links to it still work.
- Do not paste commands or version numbers you have not run or looked up. A wrong command in
  these files wastes every later session's time.
