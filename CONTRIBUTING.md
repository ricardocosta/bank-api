# Contributing

This document includes some guidelines for contributing to the project.

## 💻 Development

Clone the repository and install all the dependencies:

```
$ git clone <git-remote-url>
$ cd <location>
$ ./gradlew build
```

You can run the application locally with:

```
$ ./gradlew bootRun
```

The application will be available at http://localhost:8080.

## 💅 Linting

The project is configured with [Ktlint](https://github.com/pinterest/ktlint) for linting. It also
uses [Detekt](https://github.com/detekt/detekt) to identify code smells in the codebase.

Run `./gradlew ktlintFormat` to lint the source code, or `./gradlew ktlintCheck` to run the linting tool without
actually modifying the code. Run `./gradlew detekt` so check for code smells.

Both tools are run in a `pre-commit` git hook to make sure the codebase is kept clean of issues.

## 🐛 Testing

The project uses [Junit](https://junit.org/junit5/) to run the tests.

The tests can be run with `./gradlew test`. After each test run, a coverage report is generated and made available in
the `coverage` directory.

## 🚧 Committing

This project uses the [Conventional Commits](https://www.conventionalcommits.org/) specification. This means that commit
messages must adhere to the specification, so that they stay consistent and can be used to automatically generate change
logs when releasing new versions.

The following types of commits are supported:

- `build`: Changes that affect the build system or external dependencies.
- `chore`: Common tasks that do not need to be tracked, i.e. update dependencies.
- `ci`: Changes to the CI configuration files and scripts.
- `docs`: Documentation only changes.
- `feat`: A new feature.
- `fix`: A bug fix.
- `perf`: A code change that improves performance.
- `refactor`: A code change that neither fixes a bug nor adds a feature.
- `revert`: Revert a previous commit
- `style`: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc).
- `test`: Adding missing tests or correcting existing tests.

Any breaking changes that are introduced during development need to be tracked in the commit message. To do so, we need
to add a bang (`!`) to the type of commit (e.g. `feat!`) and `BREAKING CHANGES:` as part of the commit message:

```
feat!: Breaking something
BREAKING CHANGE: `extends` key in config file is now used for extending other config files
```

### Rules

The commits are checked and validated using [`commitlint`](https://github.com/NetrisTV/gradle-commitlint-plugin). The
linter can be configured to some extent but there are some things that need to be documented in order to keep the commit
history as clean and as consistent as possible:

- Commits must be prefixed with a type. The supported types are listed in the above section.
- Breaking changes must be indicated in the type of commit.
- Subjects must use sentence-case.
- Subjects must use imperative tense, as if issuing a command or making a request.
- Commits should have a body explaining why they are needed, what they are doing and how.
- Commits should have a reference to the JIRA issue, if one exists.

## 🔀 Pull Requests

Pull requests should have a clear scope and be concise. When applicable, include examples (e.g. API responses) that
better demonstrates the newly introduced features or bug fixes.
