# Evidence Matrix & Claims Ledger

**Release Reference:** `OSIRIS-LIVLM-BETA-0.1.0-REF`  
**Target Release:** `v0.1.0-beta.1`  
**Release Designation:** `v0.1.0-beta.1 REFERENCE IMPLEMENTATION (NOT DEPLOYMENT-ATTESTED)`  
**Zenodo DOI:** [10.5281/zenodo.22980008](https://doi.org/10.5281/zenodo.22980008)  
**Epistemic Rule:** Strict demarcation of claims; adverse-first precedence; cross-plane non-substitution (`SCIENCE ⊬ DEPLOYMENT`; `DEPLOYMENT ⊬ SCIENCE`).  

---

## 1. Master Evidence Matrix

| Claim | Artifact | Evidence Plane | Status | Reproduction / Source | Deployment Relevance |
|---|---|---|---|---|---|
| **Canonical Serialization Determinism** | `src/osiris_governance/canonical.py` | Governance / Code | `VERIFIED` | `pytest tests/test_canonical_binding.py` | Required for cryptographic permit hashing |
| **Float Rejection in JSON Proposals** | `src/osiris_governance/canonical.py` | Governance / Policy | `VERIFIED` | `test_reject_float_in_strict_parse` | Guarantees identical cross-architecture parsing |
| **Duplicate Key Rejection** | `src/osiris_governance/canonical.py` | Governance / Policy | `VERIFIED` | `test_reject_duplicate_key_in_strict_parse` | Prevents smuggling attacks during canonicalization |
| **Pre-Adapter Capability Enforcement** | `src/osiris_governance/governor.py` | Runtime Security | `VERIFIED` | `pytest tests/test_replay_governor.py` | Proves zero adapter code runs without valid permit |
| **In-Epoch Nonce Deduplication** | `src/osiris_governance/governor.py` | Runtime Security | `VERIFIED` | `test_inv_10_in_epoch_nonce_deduplication` | Prevents execution replay within an epoch |
| **Append-Only Hash Chain Integrity** | `src/osiris_governance/ledger.py` | Reproducibility | `VERIFIED` | `test_ledger_append_and_hash_chain_integrity` | Verifies tamper-evident audit history |
| **Evidence Non-Substitution Invariant** | `src/osiris_governance/ledger.py` | Governance / Policy | `VERIFIED` | `test_cross_plane_substitution_detected` | Blocks runtime evidence from asserting release |
| **Adverse-First Precedence Resolution** | `src/osiris_governance/adjudication.py` | Governance / Logic | `VERIFIED` | `test_s_deployed_fail_precedence` | Adverse finding forces `FALSE`/`BLOCKED` |
| **Mutable Reference Rejection** | `src/osiris_governance/ledger.py` | Governance / Policy | `VERIFIED` | `test_reject_mutable_references` | Rejects `:latest` and branch names |
| **LivLM HTTP Service Liveness & Ready** | `src/osiris_governance/livlm_service.py` | Runtime / Service | `VERIFIED` | `test_livlm_http_server_endpoints` | Confirms `/healthz` and `/readyz` operational |
| **Container Build Readiness** | `Dockerfile`, `cloudbuild.yaml` | Build / Packaging | `VERIFIED` | Multi-stage Docker build audit | Prerequisite for Cloud Run deployment |
| **Operational Confinement Campaign** | `evidence/operational/REAL-CAMPAIGN-20260926T160434Z/` | Operational (OEP) | `FALSE` | Live seccomp/egress/tmp probe failure | **Falsifying operational evidence for current config** |
| **Google Cloud Run Deployment** | `CLOUD_DEPLOYMENT_RECORD.md` | Cloud Infrastructure | `BLOCKED` | `gcloud run services list` (suspended) | Explicitly blocks $S_{\text{deployed}} = \text{TRUE}$ |
| **DNA-Lang Quantum Circuit Encoding** | `/home/enki/osiris_livlm.py` | Scientific / Simulation | `SIMULATED` | `python3 osiris_livlm.py` (Aer) | Demonstrates circuit synthesis only |
| **Physical IBM Heron QPU Execution** | `flywheel-2026/PROPOSAL.md` | Hardware Execution | `HYPOTHESIS` / `PLANNED` | Grant Milestone 2 execution | Does NOT assert current physical speedup |
| **Cross-Plane Provenance Linking** | `evidence/provenance/XPL-2026-09-26-001/` | Provenance (XPL) | `VERIFIED` | `ROOT_HASH.json` (`sha256:49b3...`) | Links OEP and SEP to shared artifact lineage |

---

## 2. Invariant Adjudication Trace

```text
============================================================
OSIRIS MASTER ADJUDICATION EVALUATION
============================================================

1. Software correctness terms:
   - canonical_binding == PASS (14/14 RFC tests)
   - capability_governor == PASS (13/13 security invariants)
   - evidence_ledger == PASS (11/11 tests)
   - http_service == PASS (5/5 tests)
   => S_software = TRUE
   => LocalReferenceReleaseAllowed = TRUE

2. Operational deployment terms (REAL-CAMPAIGN-20260926T160434Z):
   - seccomp_filter == FAIL (mode 0)
   - network_egress == FAIL (attempted 169.254.169.254:80)
   - tmp_confinement == FAIL (unconfined write)
   => S_deployed = FALSE
   => CloudDeploymentAllowed = FALSE (BLOCKED)
   => ConfirmatoryExecution = PROHIBITED

3. Scientific efficacy terms (SEP-QF-2026-001):
   - QF-001 (Quantum Negentropy Scaling) = HYPOTHESIS
   - QF-002 (Statevector Emulation) = SIMULATION_RESULT
   - QF-003 (Canonical Invariance) = MEASURED
   - QF-004 (Security Invariants) = REPRODUCED
   - QF-005 (IBM Heron Hardware Drift) = PROSPECTIVE_QPU_TEST
   => Evaluated on independent scientific plane
   => STRICTLY FORBIDDEN from substituting for S_deployed

4. Cross-Plane Provenance (XPL-2026-09-26-001):
   - Shared artifact: sha256:8df61dc... (commit: 8df61dc / 8b3f047)
   - Operational root: sha256:fcb966e... (S_deployed = FALSE)
   - Scientific root: sha256:3a4b918... (Independently evaluated)
   - Provenance root: sha256:49b3d3e...
   - Cross-plane substitution: FORBIDDEN

FINAL ADJUDICATION DISPOSITION:
   RELEASE_READY_WITH_EXPLICIT_LIMITATIONS
   (Reference implementation verified; deployment gate FALSE/BLOCKED; scientific hypotheses preserved).
============================================================
```
