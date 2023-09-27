## Actions / Feedback
The `creative-central` also has an actions/feedback system, that is, execute
certain actions when the player accepts, successfully downloads, rejects or
fails to download the server's resource-pack.

The actions can be: send a message to the player, send a title to the player,
kick the player.

### Configuring

Open `config.yml` and go to the `feedback` section, you will find something
like this:
```yaml
feedback:
  accepted: ...
  success: ...
  declined: ...
  failed: ...
```

Those subsections have a list of actions to execute when the player:
- `accepted`: accepted the resource-pack
- `success`: finished downloading the resource-pack
- `declined`: declined the resource-pack
- `failed`: failed to download the resource-pack

The supported actions are:
- `message`: Just sends a chat message to the player
- `title`: Sends a title to the player, has `title`, `subtitle`,
`fade-in`, `stay` and `fade-out` options
- `kick`: Just kicks the player with the given message, which can be multi-line

Examples:
```yaml
feedback:
  accepted:
      - message: 'You just accepted the resource-pack!'
      - message: 'Yay!'
  success:
      - title:
          title: 'You just downloaded the resource-pack!'
          subtitle: 'Yay!'
          fade-in: 20
          stay: 20
          fade-out: 20
      - message: 'You are so cool!'
      - message: 'Yay!'
  declined:
      - kick: 'Please accept!'
  failed:
      - kick: 'You failed to download the resource-pack :('
```