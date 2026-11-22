# Finest add-ons

Home Assistant add-ons for [Finest](https://app.finest.rs).

| Add-on | What it does |
| --- | --- |
| [Finest](finest) | Installs the Finest integration for the Finest Home app and keeps it up to date. |
| [Traefik SSL proxy](traefik) | TLS reverse proxy that serves the certificate the Finest integration issues, with TURNS passthrough to Coturn. Used on Finest installations. |
| [Coturn](coturn) | TURN/STUN server for live camera video when a direct connection isn't possible. Used on Finest installations. |

## Add the repository

In Home Assistant, open **Settings** > **Add-ons** > **Add-on store**, open the menu in the
top right corner, choose **Repositories** and add:

`https://github.com/finest-home/addons`

Add-ons need Home Assistant OS or a Supervised installation. On other installations, install
the integration with HACS or by hand: see [Install the integration](https://docs.finest.rs/get-started/install).

## Problems and ideas

Report them in [finest-home/support](https://github.com/finest-home/support/issues).
