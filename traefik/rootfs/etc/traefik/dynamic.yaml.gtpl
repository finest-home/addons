{{- if .variables.turn_sni }}
tcp:
  routers:
    turn:
      entryPoints:
        - websecure
      rule: "HostSNI(`{{ .variables.turn_sni }}`)"
      service: coturn
      tls:
        passthrough: true
  services:
    coturn:
      loadBalancer:
        servers:
          - address: "{{ .variables.turn_upstream }}:{{ .options.turn_upstream_port }}"
{{- end }}
http:
  routers:
    ha:
      entryPoints:
        - websecure
      rule: "{{ .variables.host_rule }}"
      service: homeassistant
{{- if .options.hsts }}
      middlewares:
        - hsts
{{- end }}
      # The certificate comes from /ssl via tls.yaml (nestfi-tls-config),
      # matched to the request by SNI.
      tls: {}
  services:
    homeassistant:
      loadBalancer:
        servers:
          - url: "http://homeassistant.local.hass.io:{{ .variables.port }}"
        passHostHeader: true
{{- if .options.hsts }}
  middlewares:
    hsts:
      headers:
        stsSeconds: 31536000
        stsIncludeSubdomains: true
{{- end }}
