# Simply No Report

This mod gives the option to server admins to disable chat reporting, in a non-intrusive way. 
It is **disabled by default** to let everyone decide what they want to do.

Configuration is done through gamerules.

This mod can be found on [Curseforge](https://www.curseforge.com/minecraft/mc-mods/simply-no-report/)
and [Modrinth](https://www.modrinth.com/mods/simply-no-report/).

## Usage

This mod is server side only. It can be enabled by setting the `disableChatReport` gamerule to `true`.

Turning the mod off may cause one client to get disconnected to catch up with the cryptographic chain.
Everything will be back to normal after that.

## Strategies

There are currently two strategies for disabling chat reporting:
- `STRIP_SIGNATURE` (default): removes the signature before sending the message to the clients.
  The signature is still verified by the server. Messages will be accompanied by a red border
  signifying it wasn't signed.
- `CONVERT_TO_SERVER_MESSAGE`: converts the message to a server message, effectively removing any player
  information tied to the message. It *should work* with most chat plugins, if not please open an issue!
  Messages will be accompanied by a gray border signifying it is a system message.

The strategy can be changed by setting the `disableChatReportStrategy` gamerule. 

No matter what strategy you choose, we highly recommend you to clearly communicate to your users that chat
reporting is disabled.