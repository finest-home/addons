ARG BUILD_FROM
FROM ${BUILD_FROM}

# BUILD_VERSION is the add-on version (config.yaml), which is the integration
# release to install. The published release lays the integration out the way
# HACS does: custom_components/finest_app.
ARG BUILD_VERSION
RUN set -eux; \
    mkdir -p /opt/finest; \
    curl -fsSL "https://github.com/finest-home/integration/archive/refs/tags/${BUILD_VERSION}.tar.gz" \
      | tar -xz -C /tmp; \
    mv /tmp/integration-*/custom_components/finest_app /opt/finest/finest_app; \
    rm -rf /tmp/integration-*; \
    test -f /opt/finest/finest_app/manifest.json

COPY rootfs /
