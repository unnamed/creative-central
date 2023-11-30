## Oraxen

`creative-central` supports Oraxen by default, it can take the resource-pack
that Oraxen generates and merge it with the resource-pack that `creative-central`
generates, this way you can use Oraxen and any plugin that uses `creative-central`
at the same time.

### Configuration

You need to make some changes to Oraxen's configuration in order to make it work
with `creative-central`.

#### 1. Disable upload

First of all, disable Oraxen resource-pack upload in `settings.yml`:
<!--@formatter:off-->
```yaml
Pack:
  ...
  upload:
    enabled: false # <- Set to false here
    type: ...
    ...
```
<!--@formatter:on-->

creative-central can upload resource-packs too, so this shouldn't be a problem.

#### 2. Disable receive

Then, you need to set `Pack.receive.enabled` to `false`. `creative-central` can also
manage actions on resource-pack feedback, so this shouldn't be a problem either.

```yaml
Pack:
  ...
  receive:
    enabled: false # <- Set to false here
    ...
```

And that's it, you can now use Oraxen and creative-central at the same time!