## Development

Instructions on how to develop in this project.

### Detekt

Run Detekt on all library modules:

```
./gradlew detektAll
```

Run Detekt on all library modules with auto-formatting enabled

```
./gradlew detektAll -PdetektAutoFix=true
```

Generate Detekt baseline

```
./gradlew detektGenerateBaseline
```