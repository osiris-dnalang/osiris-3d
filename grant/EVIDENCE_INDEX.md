# Cryptographic Evidence Index: Quantum Flywheel Grant

**Grant Submission:** BlueQubit Quantum Flywheel Open Call 2026  
**Document:** Cryptographic Evidence Index & Artifact Provenance Register  
**Release Reference:** `OSIRIS-LIVLM-BETA-0.1.0-REF`  
**Ledger Epoch:** `livlm-beta-epoch-1`  

---

## 1. Evidence Registry & Binding Coordinates

Every technical claim in the BlueQubit Grant proposal is anchored to four immutable coordinates:
$$\text{Scope}(E) = (\text{Artifact Digest}, \, \text{Environment Digest}, \, \text{Time Interval}, \, \text{Authorizing Policy})$$

| Claim ID | Claim Description | Evidence Plane | Status | Primary Artifact Reference | Cryptographic SHA-256 Digest |
|---|---|---|---|---|---|
| **EVID-01** | Canonical serialization enforces byte determinism | Governance | `VERIFIED` | `src/osiris_governance/canonical.py` | `sha256:7216ee342f10...` |
| **EVID-02** | Rejection of IEEE 754 floats in JSON proposals | Governance | `VERIFIED` | `tests/test_canonical_binding.py` | `sha256:887910243abb...` |
| **EVID-03** | Capability governor enforces pre-adapter isolation | Runtime Security | `VERIFIED` | `src/osiris_governance/governor.py` | `sha256:a418ff223401...` |
| **EVID-04** | In-epoch nonce deduplication prevents replay | Runtime Security | `VERIFIED` | `tests/test_replay_governor.py` | `sha256:d129038ba981...` |
| **EVID-05** | Dynamic evidence ledger maintains unbroken SHA-256 chain | Reproducibility | `VERIFIED` | `src/osiris_governance/ledger.py` | `sha256:bb019481eeaa...` |
| **EVID-06** | Adverse-first precedence resolves contradictory findings | Governance | `VERIFIED` | `tests/test_dynamic_ledger.py` | `sha256:c09931885ba2...` |
| **EVID-07** | Rejection of mutable references (`latest`, `main`) | Governance | `VERIFIED` | `tests/test_dynamic_ledger.py` | `sha256:c09931885ba2...` |
| **EVID-08** | LivLM HTTP REST service handles intent & replay | Runtime / API | `VERIFIED` | `src/osiris_governance/livlm_service.py` | `sha256:c2d61ee200af...` |
| **EVID-09** | Bio-inspired quantum circuit generation (DNA-Lang) | Scientific / Exp | `SIMULATED` | `/home/enki/osiris_livlm.py` | `sha256:326618ea4d55...` |
| **EVID-10** | Google Cloud Run container build readiness | Deployment | `VERIFIED` | `Dockerfile`, `cloudbuild.yaml` | `sha256:3e7237386afd...` |
| **EVID-11** | Google Cloud Run live deployment | Deployment | `UNVERIFIED` | `CLOUD_DEPLOYMENT_RECORD.md` | `BLOCKED (GCP Consumer Suspended)` |
| **EVID-12** | Physical QPU benchmark (IBM Heron 156-qubit) | Hardware Exec | `HYPOTHESIS` | `flywheel-2026/PROPOSAL.md` | `Planned Milestone 2` |

---

## 2. Invariant Audit Rules

Under the **Evidence Non-Substitution Invariant**:
1. Evidence `EVID-01` through `EVID-08` proves **software correctness and governance enforcement**. It does *not* prove `EVID-12` (physical QPU speedup).
2. Evidence `EVID-10` proves **container build readiness**. It does *not* prove `EVID-11` (live Google Cloud deployment).
3. Evidence `EVID-09` proves **simulation functionality**. It does *not* prove physical laboratory reproducibility.

Any attempt by a reviewer or evaluator to substitute software verification for hardware or deployment claims is mathematically and structurally rejected by the OSIRIS specification.

---

## 3. Verification & Independent Audit Instructions

To verify this entire evidence package locally:

```bash
# 1. Clone repository at release commit
git clone https://github.com/osiris-dnalang/osiris-governance.git
cd osiris-governance
git checkout v0.1.0-beta.1

# 2. Run the automated test suite
PYTHONPATH=src pytest -o cache_dir=/tmp/.pytest_cache -v

# 3. Verify SHA-256 release manifest checksums
sha256sum -c <<EOF
3e7237386afd864e5e63ea937d002951a9beaa70c25368a46058d4c4c27645f5  Dockerfile
dd4d7873335d85eac9dc340f67ceec056fbd55baac983d0aa940437ccad97d22  cloudbuild.yaml
2e3f5df207ef1f736e19448c8624c08ec9bfdc0eff7638de8cf994ad4408387f  BETA_TEST_REPORT.md
EOF

# 4. Start the LivLM service and query provenance
python3 -m osiris_governance.livlm_service &
PID=$!
sleep 1
curl -s http://127.0.0.1:8080/v1/provenance | jq .
kill $PID
```
