# Backend rules
There is NO backend. Do not create servers, REST/GraphQL APIs, sockets, analytics, crash reporting or any network code. Do not add the INTERNET permission. Services in this app are only Android system bindings (accessibility, notification listener) and a device-admin receiver, as specified in `docs/05`.
