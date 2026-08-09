# Releasing

Maintainer notes for publishing `at-api` to the
[AutoTrader Maven repository](https://github.com/stocks-developer/autotrader-maven-repo), which is
what the [Java library setup guide](https://stocksdeveloper.in/documentation/client-setup/java-library/)
tells users to add to their build.

Last verified: **2026-08-08** (release 3.3.0).

## What gets released

`at-api` and `public-library` are **always released together and share one version number**. `at-api`
depends on `public-library` at that exact version, so releasing one without the other produces a POM
pointing at an artifact nobody can resolve.

Both projects normally sit at an internal version (`1.0.0`, with `project.version` commented out in
`build.gradle`) and are switched to the public version only for the duration of a release.

## Before you start

- Both projects are committed and pushed. The published jars are built from the working tree.
- Check what is already published — the version directories under `com/dakshata/at-api/` in the maven
  repo are the source of truth. Never reuse a version.
- Version line is **3.x** here, independent of the Python and C# libraries (which are on 1.x). Do not
  try to align them. New API calls are a minor bump (3.2.0 -> 3.3.0).

## Steps

Stocks Developer maintainers: this is fully automated by an internal release script — one command
takes the version, publishes both artifacts, restores the build files and updates the docs. Ask
before cutting a release by hand.

The equivalent manual sequence, for reference:

1. In `public-library/build.gradle`, uncomment `project.version` and set the release version.
   Publish it: `./gradlew publishAllPublicationsToGithubRepository`
2. In `at-api/build.gradle`, do the same, **and** point the `public-library` dependency at the new
   version. Publish: `./gradlew publishAllPublicationsToGithubRepository`
3. **Revert both `build.gradle` files** to the internal state (re-comment `project.version`, restore
   the dependency entry) and commit that revert, so day-to-day builds stay internal.
4. Commit and push the maven repository working tree. **The release is public the moment that push
   lands.**
5. Bump the version shown in the Gradle and Maven snippets on the website's Java library page.

## Verify the release

The published sources jar is a zip — check the new API is actually in it before announcing:

```bash
unzip -p at-api-<version>-sources.jar 'com/dakshata/autotrader/api/IAutoTrader.java' | grep 'yourNewMethod'
```

And confirm the POM's `public-library` dependency shows the same version you just released.

## Gotchas

- **Always re-check that `build.gradle` was reverted**, especially after a failed release. Leaving
  `project.version` uncommented means the next internal build quietly produces a public version.
- **The maven repo push can be rejected** if someone else pushed to it in the meantime. The artifacts
  are already committed locally at that point, so recovery is just a rebase and a second push — the
  release commit only adds files under `com/dakshata/`, so it replays cleanly.
- **There is no jar to upload anywhere.** An older process ended with a fat jar copied to cloud
  storage; the Java setup page now ships Gradle/Maven coordinates only, and that step is retired.
