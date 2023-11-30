## ItemsAdder

`creative-central` supports ItemsAdder by default, it can take the resource-pack
that ItemsAdder generates and merge it with the resource-pack that `creative-central`
generates, this way you can use ItemsAdder and any plugin that uses `creative-central`
at the same time.

### Configuration

You need to make some changes to ItemsAdder's configuration in order to make it work
with `creative-central`.

#### 1. Disable hosting

First of all, disable ItemsAdder resource-pack hosting in `config.yml`. By default,
it shouldn't be enabled, but if you have it enabled, you need to disable it:
<!--@formatter:off-->
```yaml
resource-pack:
  hosting:
    no-host:
      enabled: true # <-- Set this to true
    self-host:
      enabled: false # <-- Set this to false
      server-ip: ...
      pack-port: ...
    external-host:
      enabled: false # <-- Set this to false
      url: ...
      skip-url-file-type-check___DONT_ASK_HELP_IF_SET_TRUE: false
  ...
```
<!--@formatter:on-->

creative-central can upload/host resource-packs too, so this shouldn't be a problem.

#### 2. Disable apply-on-join

Then, you need to set `resource-pack.apply-on-join` to `false`. `creative-central` can also
send the resource-pack to players when they join, so this shouldn't be a problem either.

```yaml
resource-pack:
    ...
    apply-on-join: false
    ...
```

#### 3. Disable merging other resource-packs

Finally, you need to prevent ItemsAdder from merging other plugins' resource-pack
with its own resource-pack. This prevents a resource-pack from being merged twice.

```yaml
resource-pack:
  ...
  zip:
    ...
    merge_other_plugins_resourcepacks_folders: [] # <-- Set this to an empty list
                                                 # or just remove known plugins
                                                 # like ModelEngine from the list
```

And that's it, you can now use ItemsAdder and creative-central at the same time!