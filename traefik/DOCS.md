# Traefik SSL proxy

TLS reverse proxy for Home Assistant on Finest installations. It serves the certificate
the Finest integration issues and keeps in `/share/finest/cert` — this add-on does no ACME and sends no
DNS (lego and its RFC2136 options were removed in 2.0.0). It also SNI-passthroughs
TURNS-over-443 straight to the **Coturn** add-on, so a single host port 443 serves both
HTTPS and TURN.

## Options

| Option | Description |
|--------|-------------|
| `domains` | Hostnames that terminate TLS for Home Assistant (`e-`/`i-`) |
| `turn_domain` | The `t-<core>` SAN. Always on the cert once provisioned, regardless of `turn_domains` |
| `turn_domains` | SNI names routed to Coturn on 443. Empty ⇒ no TCP router (safe default) |
| `turn_upstream` / `turn_upstream_port` | Coturn's `listening_ip` / `tls_listening_port` |
| `certfile` / `keyfile` | PEM filenames under `/ssl`, where the add-on copies the integration's certificate |
| `hsts` | Strict-Transport-Security value (empty disables it) |
| `proxy_protocol_trusted_ips` | Upstream ingress IPs/CIDRs allowed to send PROXY protocol on 443. Empty ⇒ disabled |

## How TLS routing works

- Port 80 redirects to 443.
- Port 443 is a single Traefik entrypoint. An SNI matching `turn_domains` is passed
  through **encrypted, unterminated** straight to Coturn's TLS listener. Everything else
  is routed as normal HTTPS to Home Assistant.
- The certificate always covers `domains` **and** `turn_domain` (when set) — the cert is
  provisioned once and is independent of whether TURNS routing is currently enabled, so
  flipping the Coturn toggle never triggers a reissue.
- Unlike the old nginx add-on's `ssl_reject_handshake`, an SNI that matches neither list
  gets Traefik's default self-signed cert and a 404 rather than a refused handshake —
  cosmetic difference, not a routing gap.

## Behind another ingress (PROXY protocol)

When an upstream edge proxies 443 to this add-on as a **TLS SNI passthrough**,
no HTTP headers exist on that hop, so every request would surface as the
edge's IP. PROXY protocol carries the original client address through the
passthrough:

- Upstream (Traefik example): `tcp.services.<name>.loadBalancer.proxyProtocol.version: 2`
- Here: add the upstream's IP to `proxy_protocol_trusted_ips`

Order matters: set the option here **first** (accepting the header is
signature-detected, so plain connections keep working), then enable sending
on the upstream — an upstream sending PROXY protocol at an add-on that does
not expect it breaks the TLS handshake. Headers from IPs not in the list are
ignored, so nothing else can spoof a client address. The Coturn TURNS
passthrough is unaffected: the header is consumed on the entrypoint and never
forwarded to Coturn.

## Certificates

The Finest integration's cert manager issues and renews the certificate (its DNS-01
challenge goes to backend-api over HTTPS, so a router that intercepts port 53 cannot break
renewal) and writes `fullchain.pem`/`privkey.pem` to `/share/finest/cert`, because `/ssl`
is read-only inside Home Assistant. `nestfi-tls-config` copies them into `/ssl` as
`certfile`/`keyfile` (key first, for Coturn) and inline into
`/etc/traefik/dynamic/tls.yaml`, at start and again whenever the share folder changes
(the `certdump` service watches it) — inline, because Traefik does not re-read a
certificate file whose surrounding config is unchanged. Routers match the certificate by
SNI. Coturn watches `/ssl` itself and restarts when the certificate changes.

Upgrading from 1.x: on start, 2.0.0 deletes lego's leftovers in `/data` (`acme.json` and
`.rfc2136_secret`). A home must run integration 1.6.10 or newer, which renews the
certificate; until it does, nothing renews it.

## Home Assistant HTTP

Home Assistant must trust this add-on as a reverse proxy, or every request is
rejected/attributed to the add-on's IP.

On Home Assistant **2026.8 and newer** the `http` settings live in the config
store (`.storage/http`, managed via the `http/config/*` websocket commands);
a leftover YAML block is migrated once and then ignored. The Finest
integration ensures the proxy pair automatically (`http_proxy_config.py`).
Without the integration, configure it through the store.

On **older** Home Assistant, configure it in `configuration.yaml`:

```yaml
http:
  use_x_forwarded_for: true
  trusted_proxies:
    - 172.30.32.0/23
```

Use the whole Supervisor network, not just the `172.30.33.0/24` add-on
allocation range: depending on how the request reaches Core it can present as
the bridge gateway `172.30.32.1`, and a /24 then answers every request with
`400 Bad Request`.

For WebRTC TURN/STUN, install the **Coturn** add-on from the same repository.
