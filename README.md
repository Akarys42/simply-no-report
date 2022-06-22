# Simply No Report

This mod gives the option to server admins to disable chat reporting, in a non-intrusive way. 
It is **disabled by default** to let everyone decide what they want to do.

## Usage

This mod is server side only. It can be enabled by setting the `disableChatReport` gamerule to `true`.

## Strategies

There are currently two strategies for disabling chat reporting:
- `STRIP_SIGNATURE` (default): removes the signature before sending the message to the clients. 
The signature is still verified by the server. This is the recommended strategy, although it may cause some
issues further down the road. In 1.19.1pre1, each message received by the client will trigger a warning log.
- `CONVERT_TO_SERVER_MESSAGE`: converts the message to a server message, effectively removing any player
information tied to the message. It will probably break most mods modifying the chat.

## Operator Bypass

If you are using the `STRIP_SIGNATURE` strategy, operators will continue to receive signed messages.
It can be disabled using the `sendSignaturesToOperators` gamerule.

The strategy can be changed by setting the `disableChatReportStrategy` gamerule.