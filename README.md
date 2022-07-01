# Simply No Report

This mod gives the option to server admins to disable chat reporting, in a non-intrusive way. 
It is **disabled by default** to let everyone decide what they want to do.

Configuration is done through gamerules.

## Usage

This mod is server side only. It can be enabled by setting the `disableChatReport` gamerule to `true`.

## Strategies

There are currently two strategies for disabling chat reporting:
- `STRIP_SIGNATURE` (default): removes the signature before sending the message to the clients.
  The signature is still verified by the server. In 1.19.1-pre2, messages will be accompanied by a red border
  signifying it wasn't signed.
- `CONVERT_TO_SERVER_MESSAGE`: converts the message to a server message, effectively removing any player
  information tied to the message. It *should work* with most chat plugins, if not please open an issue!
  We also recommend you to clearly indicate to your users reporting is disabled.

The strategy can be changed by setting the `disableChatReportStrategy` gamerule.

## Operator Bypass

Operators will continue to receive signed messages. This feature can be disabled using the 
`sendSignaturesToOperators` gamerule.