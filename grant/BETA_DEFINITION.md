# Living Language Model Beta: Definition, Scope & Operational Boundaries

**Release Identifier:** `OSIRIS-LIVLM-BETA-0.1.0-REF`  
**Target Release:** `v0.1.0-beta.1`  
**Epistemic Disposition:** `RELEASE_READY_WITH_EXPLICIT_LIMITATIONS`  
**Target Audience:** External Beta Evaluators, Pilot Partners, Grant Reviewers  

---

## 1. What is the Living Language Model Beta?

The Living Language Model Beta (`v0.1.0-beta.1`) is a governed, reproducible reference service that allows developers and researchers to submit natural-language intents, inspect deterministic circuit synthesis proposals, execute verified replay fixtures, and review tamper-evident cryptographic evidence logs.

The beta is explicitly designed as a **fail-closed, read-mostly, and bounded-execution system**. It enforces mathematical and governance boundaries to ensure that untrusted external inputs cannot trigger unauthorized cloud mutations, execute unsandboxed code, or corrupt shared state.

---

## 2. Supported Capabilities (What Beta Users CAN Do)

| Capability | Interface | Description | Governance Policy |
|---|---|---|---|
| **System Health & Readiness** | `GET /healthz`, `GET /readyz` | Inspect service liveness, ledger chain integrity, and software revision. | Unauthenticated; read-only. |
| **Release Provenance Inspection** | `GET /v1/provenance` | Retrieve machine-readable provenance, git SHA, and epistemic boundaries. | Unauthenticated; read-only. |
| **Intent Interpretation** | `POST /v1/intent` | Submit natural-language requests; receive structured intent proposals and execution receipts. | Bounded execution; zero side effects permitted (`side_effects_allowed=false`). |
| **Deterministic Replay** | `POST /v1/replay` | Re-run canonical test fixtures against the in-memory `ReplayAdapter` using cryptographic permits. | In-epoch nonce deduplication; permit matching required. |
| **Audit Ledger Traversal** | `GET /v1/ledger/events` | Retrieve full or paginated sequence of hash-chained audit events. | Cryptographic verification of SHA-256 chain links. |
| **Claim Adjudication** | `POST /v1/claims/evaluate` | Test cited evidence packages against claim types under the Evidence Non-Substitution Invariant. | Adverse-first precedence; cross-plane substitution blocked. |

---

## 3. Prohibited Actions (What Beta Users CANNOT Do)

To guarantee system stability and prevent evidence corruption, the following actions are strictly blocked:
1. **Unbounded Code Execution:** The service contains zero subprocess dispatchers, shell execution tools, or dynamic code evaluation (`eval`/`exec`).
2. **Filesystem Writes:** The application runs as an unprivileged user (`UID 10001`) with a read-only root filesystem.
3. **External Network Egress:** The beta execution adapter possesses zero network authority and cannot communicate with arbitrary third-party endpoints.
4. **Direct QPU Mutation:** Physical hardware execution is restricted to authorized backend worker pipelines; beta users cannot reconfigure physical QPU pulse schedules or calibration tables.
5. **Nonce Re-use:** Replaying the same request nonce within an active process epoch is immediately rejected (`NonceReplayDetected`).

---

## 4. API Request & Response Contracts

### 4.1 Intent Evaluation (`POST /v1/intent`)
**Request:**
```json
{
  "text": "Audit the ledger and verify cryptographic chain integrity",
  "client_nonce": "eval-client-2026-09-26-001"
}
```

**Response (HTTP 200 OK):**
```json
{
  "execution_id": "evt-000002",
  "release_id": "OSIRIS-LIVLM-BETA-0.1.0-REF",
  "software_version": "0.1.0-beta.1",
  "intent": {
    "type": "AUDIT",
    "confidence": 0.95,
    "required_capability": "ledger.read"
  },
  "request_hash": "sha256:7b91c78b4a09...",
  "evidence_digest": "sha256:4f8812e9b3a1...",
  "epistemic_status": "SIMULATED_OFFLINE_REFERENCE",
  "governance": {
    "policy_ref": "livlm-beta-policy@1.0",
    "side_effects_allowed": false,
    "chain_valid": true
  },
  "result": {
    "events_count": 2,
    "chain_valid": true,
    "latest_digest": "sha256:4f8812e9b3a1..."
  }
}
```

### 4.2 Deterministic Replay (`POST /v1/replay`)
**Request:**
```json
{
  "fixture_id": "echo-v1",
  "arguments": {
    "fixture_id": "echo-v1",
    "message": "hello"
  },
  "client_nonce": "replay-nonce-001"
}
```

**Response (HTTP 200 OK):**
```json
{
  "status": "SUCCESS",
  "proposal_sha256": "sha256:b8193f1efc838...",
  "fixture_id": "echo-v1",
  "nonce": "replay-nonce-001",
  "evidence_event_id": "evt-000003",
  "payload": {
    "status": "SUCCESS",
    "message": "hello"
  },
  "release_id": "OSIRIS-LIVLM-BETA-0.1.0-REF",
  "epistemic_status": "REPRODUCED_OFFLINE"
}
```

---

## 5. Deployment Options for Beta Evaluators

1. **Local Python Execution:**
   ```bash
   git clone https://github.com/osiris-dnalang/osiris-governance.git
   cd osiris-governance
   pip install -e .
   python3 -m osiris_governance.livlm_service
   ```
2. **Local Container Execution (Docker):**
   ```bash
   docker build -t livlm-beta:0.1.0 .
   docker run -p 8080:8080 --read-only --cap-drop=ALL livlm-beta:0.1.0
   ```
3. **Google Cloud Run (Hosted Beta):**
   - Packaged and pre-configured via `cloudbuild.yaml`.
   - Blocked in current environment due to GCP project consumer suspension; unblocks immediately upon account reinstatement.
