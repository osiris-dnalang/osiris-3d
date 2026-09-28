# Living Language Model Beta (v0.1.0-beta.1)

**Release ID:** `OSIRIS-LIVLM-BETA-0.1.0-REF`  
**Git Commit:** `8b3f047`  
**Release Tag:** `v0.1.0-beta.1`  
**Release Designation:** `v0.1.0-beta.1 REFERENCE IMPLEMENTATION (NOT DEPLOYMENT-ATTESTED)`  
**Zenodo DOI:** [10.5281/zenodo.22980008](https://doi.org/10.5281/zenodo.22980008)  
**Grant Proposal DOI:** [10.5281/zenodo.22863245](https://doi.org/10.5281/zenodo.22863245)  
**Epistemic Disposition:** `RELEASE_READY_WITH_EXPLICIT_LIMITATIONS`  
**Operational Status:** `S_deployed = FALSE (REAL-CAMPAIGN-20260926T160434Z)`  
**Cross-Plane Record:** `XPL-2026-09-26-001` (Strict Non-Substitution: `SCIENCE ⊬ DEPLOYMENT`)  

Welcome to the **Living Language Model Beta** developer reference release. This document provides clear, honest answers to the 10 most critical questions about this release, its capabilities, operational boundaries, and verification procedures.

---

## 1. What is this software?

The **Living Language Model (LivLM)** is an experimental, governed language-to-computation system developed under the OSIRIS project. It pairs a deterministic bio-computational intent engine with a fail-closed governance control plane.

Unlike typical LLM wrappers, every LivLM execution is:
- **Mechanically Governed:** Bounded by strict capability contracts prior to adapter execution.
- **Byte-Deterministic:** Serialized via the `OSIRIS-CANONICAL-JSON-V1` profile before hashing.
- **Cryptographically Provenanced:** Every request, permit, output, and policy decision is permanently anchored in a SHA-256 append-only evidence ledger.

---

## 2. How do I run it?

### Option A: Direct Python Execution (Standard Library)
The service requires Python 3.12+ and has zero heavy external dependencies:
```bash
# Clone the repository
git clone https://github.com/osiris-dnalang/osiris-governance.git
cd osiris-governance

# Install editable package
pip install -e .

# Run the beta HTTP service (defaults to port 8080)
python3 -m osiris_governance.livlm_service
```

### Option B: Local Container Execution (Docker / Podman)
```bash
# Build the minimal unprivileged container
docker build -t livlm-service:0.1.0 .

# Run with rootless user and read-only filesystem
docker run -p 8080:8080 --read-only --cap-drop=ALL livlm-service:0.1.0
```

---

## 3. What endpoints are available?

| Method | Endpoint | Description | Auth Requirement |
|---|---|---|---|
| `GET` | `/healthz` | Service liveness, software revision, and epistemic status | None (Public) |
| `GET` | `/readyz` | Readiness probe; verifies cryptographic ledger chain integrity | None (Public) |
| `GET` | `/v1/provenance` | Machine-readable release metadata, git commit, and invariants | None (Public) |
| `POST` | `/v1/intent` | Parse natural-language intent into a governed execution proposal | None (Bounded) |
| `POST` | `/v1/replay` | Execute deterministic replay fixture against `ReplayAdapter` | Replay Permit Required |
| `GET` | `/v1/ledger/events` | Inspect append-only audit events and cryptographic chain status | None (Public) |
| `POST` | `/v1/claims/evaluate` | Adjudicate evidence packages against claims under Non-Substitution | None (Public) |

---

## 4. What can I actually do with it?

External beta evaluators can:
1. **Submit Natural Language Queries:** Pass prompts to `/v1/intent` to inspect how the `IntentEngine` deterministically extracts intent classes (`BENCHMARK`, `AUDIT`, `REPLAY`, `STATUS`, `GENERATE`).
2. **Execute Verified Deterministic Replays:** Test repeatable fixtures (e.g., `echo-v1`, `benchmark-sample-v1`) through the `CapabilityGovernor`.
3. **Audit the Cryptographic Ledger:** Query `/v1/ledger/events` to verify that every execution produces a valid SHA-256 hash link chaining back to the genesis block.
4. **Evaluate Governance Claims:** Test whether specific evidence sets satisfy claims using `/v1/claims/evaluate` to observe how adverse findings or cross-plane substitutions are mechanically rejected.

---

## 5. What are the security guarantees?

1. **Pre-Adapter Execution Barrier:** Zero user code or adapter logic executes until the proposal's schema, canonical digest, scope, capability, and nonce are verified.
2. **Zero In-Epoch Nonce Replay:** If an identical nonce is presented twice within the same process epoch, execution is aborted with `NonceReplayDetected`.
3. **Least Privilege Container:** The container runs as `appuser` (UID 10001) with dropped capabilities and a read-only root filesystem.
4. **Zero Filesystem or Network Authority:** The `ReplayAdapter` operates entirely in memory, possessing no `os.system`, `subprocess`, socket, or file-write permissions.

---

## 6. How does the governance system work?

The governance plane implements three core architectural invariants:
- **`OSIRIS-CANONICAL-JSON-V1`:** Strict JSON serialization that forbids floating-point numbers, forbids duplicate keys, eliminates insubstantial whitespace, normalizes Unicode to NFC, and orders keys lexicographically.
- **Evidence Non-Substitution Invariant:** Evidence is constrained to the exact domain it was collected to prove. Runtime security evidence cannot establish scientific validity or deployment authorization.
- **Adverse-First Precedence:** When adjudicating multi-evidence gates, contradictory or adverse findings take absolute precedence over positive attestations.

---

## 7. What are the known limitations?

- **In-Memory Ledger Storage:** In this v0.1.0-beta.1 release, ledger events are retained in-memory per process instance. Persistent cloud storage backends are planned for v0.2.0.
- **Single Process Instance:** The reference beta does not include distributed consensus or multi-leader synchronization.
- **Bounded Intent Set:** Intent parsing is currently pattern-matched and regex-bounded; open-ended generative reasoning is intentionally constrained.

---

## 8. Why is Google Cloud deployment marked as BLOCKED?

During pre-release deployment discovery on 2026-09-26, our automated audit of Google Cloud infrastructure discovered that:
- Target deployment project `cs-project-nfhprhbh` is in `CONSUMER_SUSPENDED` status.
- Alternative candidate projects `google-mpf-*` are in `BILLING_DISABLED` status.

In accordance with our governing principle:
> *"Do NOT claim production readiness merely because Cloud Run deployment configuration exists. Do NOT claim $S_{\text{deployed}} = \text{TRUE}$ unless independent deployment evidence actually exists."*

The deployment status is honestly and transparently marked as **`UNVERIFIED (BLOCKED_BY_CONSUMER_SUSPENSION)`**. All container assets (`Dockerfile`, `cloudbuild.yaml`) are fully tested, packaged, and ready to deploy as soon as GCP account administrative reinstatement occurs.

---

## 9. What are the scientific claims and what is their status?

All scientific and quantum claims are strictly segregated from software verification:

| Claim | Epistemic Status | Description |
|---|---|---|
| **DNA-Lang Quantum Circuit Encoding** | `SIMULATED` | Byte angle rotation logic executed on Aer simulator. |
| **Negentropy Metric ($\Xi$)** | `SIMULATED` | Coherent information calculation evaluated classically. |
| **IBM Heron 156-Qubit QPU Speedup** | `HYPOTHESIS` / `PLANNED` | Proposed milestone under BlueQubit Quantum Flywheel Grant 2026. |
| **Physical Biological Replication** | `NOT EVALUATED` | Requires external empirical wet-lab validation. |

A passing software test suite does **not** validate physical quantum speedup or biological efficacy.

---

## 10. How do I verify the release integrity?

Run the automated test suite locally to verify all 63 security, governance, and REST API tests:
```bash
# Run pytest with isolated cache
PYTHONPATH=src pytest -o cache_dir=/tmp/.pytest_cache -v

# Verify release artifact hashes
sha256sum -c <<EOF
3e7237386afd864e5e63ea937d002951a9beaa70c25368a46058d4c4c27645f5  Dockerfile
dd4d7873335d85eac9dc340f67ceec056fbd55baac983d0aa940437ccad97d22  cloudbuild.yaml
2e3f5df207ef1f736e19448c8624c08ec9bfdc0eff7638de8cf994ad4408387f  BETA_TEST_REPORT.md
14c0aeb978f50eb5f71191ce9477debb9f8335b9c81a768fa431fa76973bd1a4  RELEASE_AUDIT.md
1533f6645b7b69ef40073476c1a409ab3c4aa688afd83fe9b9ec6b4c8fa2417f  CLOUD_DEPLOYMENT_RECORD.md
2284c78455081ba4846a0e14d96784109b027a174ce24ea36a0d449b9ed868eb  pyproject.toml
c2d61ee200af01a7d56bf22a454aab5159605d86546a66780d511dd53b992a21  src/osiris_governance/livlm_service.py
EOF
```
Expected result: **All checksums OK, 63/63 tests passing.**
