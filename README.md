# HapifyMe API – Automated Tests (Tema 20)

Acest proiect conține teste automate pentru API-ul **HapifyMe**, scrise în Java.

Proiectul validează funcționalitatea API-ului HapifyMe prin:
- un flow E2E complet de autentificare
- testarea obținerii și utilizării token-ului de autentificare

## Structura testelor

###  NewLoginTest
- Conține **E2E flow-ul complet** pentru API-ul HapifyMe

## TokenPollingTest
- Test separat pentru verificarea mecanismului de polling al token-ului
- Testul este setat cu `enabled = true`, însă **nu trece**
- Motiv probabil:
  - token-ul este returnat **instant**, iar logica de polling nu mai este necesară în acest caz

Testul a fost păstrat intenționat pentru a evidenția acest comportament al API-ului.

Din directorul proiectului:
```bash
mvn test
