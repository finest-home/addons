# Finest

Installs the [Finest integration](https://github.com/finest-home/integration), which the
[Finest Home](https://app.finest.rs) app needs, and keeps it up to date.

## How it works

- Each version of this add-on carries one version of the Finest integration.
- When the add-on starts, it copies the integration into `custom_components/finest_app`
  if it's missing or older, then restarts Home Assistant once to load it.
- When this add-on updates, it installs the new integration version the same way. Turn on
  **Auto update** to keep Finest current without doing anything.

After the first install, Home Assistant shows **Finest** under **Discovered** in
**Settings** > **Devices & services**. Select **Add** and choose who owns the home. Or let the
Finest Home app finish the setup. See [Install the integration](https://docs.finest.rs/get-started/install).

## When it changes nothing

- **HACS manages Finest.** If you installed Finest with HACS, HACS keeps it up to date and
  this add-on leaves it alone. To switch to the add-on, remove Finest from HACS first.
- **A newer version is installed.** The add-on never replaces a newer copy with an older one.

The add-on log says which one applies.

## Removing it

Uninstalling the add-on leaves the integration installed. To remove Finest itself, delete it
under **Settings** > **Devices & services**, then remove `custom_components/finest_app`.
