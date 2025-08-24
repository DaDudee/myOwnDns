# myOwnDns

My minimal DNS server built from scratch. Packets are structured, parsed, and responded according to RFC 1035. 
Compression in responses is not implemented.... yet ;)


Parses and builds DNS headers dynamically.
Handles a single Question and Answer section per DNS request.
Encodes and decodes DNS packets into their proper binary format.
Responds with A records (IPv4) for any queried domain.
Uses UDP for communication, as per DNS protocol.

