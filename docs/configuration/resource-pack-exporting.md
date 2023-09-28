## Pack Exporting/Hosting

`creative-central` relies on collecting the required assets *(textures, assets,
fonts, etc.)* on the server start-up, **compile** the resource-pack and then **export**
it.

In this section we will explore the **export** stage, `creative-central` supports
the following methods of exporting:
- **MCPacks** (External hosting) **(Default export method)**
- **Localhost** (Self hosting)
- **File** (Not hosted)
- **Folder** (Not hosted)

### MCPacks
*(Default export method)* `creative-central` can automatically upload the generated
resource-pack to [MCPacks](https://mc-packs.net/), a public & free resource-pack host.
*(Please note that we are not part of MCPacks development team)*.

To make `creative-central` do this, open `config.yml` and set `export.type`
to `mcpacks`, like this:

```yaml
export:
  type: 'mcpacks'
```

Please consider [donating to MCPacks](https://mc-packs.net/) if you use their
free hosting service!

### Localhost (Self hosting)
We can set up a resource-pack server, on your server!

*Note that this export method requires you to have access to one additional port. If
your hosting service doesn't provide you additional ports, consider using another
export method.*

To start using self-hosting, open `config.yml` and set the following options:
```yaml
export:
  type: 'localhost'
  localhost:
    # Enables the local resource-pack server, this is required even if you
    # have already set 'type' to 'localhost'
    enabled: true
    # Your server's address
    address: ''
    # The port to use
    port: 7270
    # The public address
    public-address: ''
```

The settings you may want to modify now are:
- `address`: The address that the resource-pack server will use. If you leave it
empty, the plugin will automatically detect your server's public IP address, however,
if you want to skip the detection, you can manually set it too. *Note: If you use
NAT (Network Address Translation), set this to `0.0.0.0` and pay extra attention to
the `public-address` setting explanation*
- `port`: The port that the resource-pack server will use. **It can't be the same as
your Minecraft server's port!**
- `public-address`: The public address, sent to players to ask them to download the
server's resource pack. *If you use NAT, you will have to set your public's address
or domain*

### File
Export the resource-pack to a ZIP file! However, this export type is **not hosted**,
so players will not be automatically asked to download the resource-pack.

To make `creative-central` export the resource-pack as a ZIP file, open `config.yml`
and set:
```yaml
export:
  type: 'file'
```
And that's it! The resource-pack will be created at `/plugins/creative-central/output.zip`

### Folder
`creative-central` can also export the resource-pack to a folder! This is pretty
similar to the `file` export method, and is also **not hosted**, so players will
not be automatically asked to download the resource-pack.

To make `creative-central` export the resource-pack as a folder, open `config.yml`
and set:
```yaml
export:
  type: 'folder'
```

And that's it too! The resource-pack will be created at `/plugins/creative-central/output/`