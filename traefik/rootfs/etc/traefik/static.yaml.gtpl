log:
  level: INFO
accessLog: {}

entryPoints:
  web:
    address: ":80"
    http:
      redirections:
        entryPoint:
          to: websecure
          scheme: https
  websecure:
    address: ":443"
{{- if .options.proxy_protocol_trusted_ips }}
    # PROXY protocol from an upstream SNI-passthrough ingress, so the real
    # client IP survives the extra hop. Headers from peers not in this list
    # are ignored (not trusted), so direct LAN/WAN clients are unaffected
    # and cannot spoof a source address.
    proxyProtocol:
      trustedIPs:
{{- range .options.proxy_protocol_trusted_ips }}
        - "{{ . }}"
{{- end }}
{{- end }}


providers:
  file:
    directory: /etc/traefik/dynamic
    watch: true
