# 13. Cross-Plane Provenance & Non-Substitution Invariant

**Specification Identifier:** `OSIRIS-SPEC-XPL-013`  
**Governing Standard:** OSIRIS Orthogonal State Machine Architecture  
**Status:** NORMATIVE  

---

## 1. The Cross-Plane Non-Substitution Invariant

The fundamental governing axiom of the OSIRIS evidence architecture is the **Cross-Plane Non-Substitution Invariant**:

```text
================================================================================
                    CROSS_PLANE_NON_SUBSTITUTION INVARIANT
================================================================================

1. Operational Evidence Packages (OEP) may establish ONLY operational,
   confinement, and deployment predicates.

2. Scientific Evidence Packages (SEP) may establish ONLY scientific,
   experimental, and analytical predicates.

3. Cross-Plane Provenance Packages (XPL) may establish ONLY artifact identity,
   lineage, execution relationships, and provenance integrity.

Therefore:

    SCIENCE      ⊬  DEPLOYMENT
    DEPLOYMENT   ⊬  SCIENCE
    PROVENANCE   ⊬  DEPLOYMENT
    PROVENANCE   ⊬  SCIENCE
================================================================================
```

Under this invariant:
- Passing unit tests, passing integration tests, or successful circuit simulations **do not** prove deployment authorization or runtime sandbox confinement.
- A passing runtime confinement campaign **does not** prove scientific truth, quantum advantage, or biological efficacy.
- A cryptographically valid cross-plane provenance record **does not** promote or upgrade any claim across planes; it merely binds the exact artifact lineage between them.

---

## 2. Evidence Plane Taxonomy

Every Primary Evidence Object in OSIRIS must declare an explicit `evidence_plane`:

| Plane | Evidence Object Type | Scope & Authority | Governing Invariant |
|---|---|---|---|
| **OPERATIONAL** | `RUNTIME_PROBE_RECEIPT` | Confinement, file descriptors, seccomp, network egress, sandbox boundary | Proves runtime environment security; never proves quantum or scientific claims. |
| **SCIENTIFIC** | `RAW_EXPERIMENTAL_DATA_RECEIPT` | Measurement bitstrings, simulation traces, protocol specifications | Proves empirical observation under declared protocol; never authorizes deployment. |
| **SCIENTIFIC (QPU)** | `HARDWARE_PROVIDER_RECEIPT` | IBM QPU job ID, calibration hash, execution interval, raw counts | Proves hardware execution occurred; never proves quantum advantage or deployment safety. |
| **PROVENANCE** | `CROSS_PLANE_PROVENANCE_RECORD` | SHA-256 artifact digests, git commits, relationship links | Binds operational and scientific records to shared artifacts; zero claim substitution allowed. |

---

## 3. Case Study: Freezing the Operational Deployment Failure

The utility of this architecture is demonstrated by its fail-closed handling of real deployment campaigns.

### The Real Campaign Incident: `REAL-CAMPAIGN-20260926T160434Z`
During live confinement probing of the reference deployment configuration on 2026-09-26:
- **Observed Violations:**
  1. Seccomp filter disabled (`mode = 0`)
  2. Unauthorized network egress attempt detected
  3. Unconfined `/tmp` filesystem write detected
- **Adjudication Outcome:**
  $$\mathbf{S}_{\text{deployed}} = \mathbf{FALSE}$$
- **Release Consequence:**
  $$\text{Cloud Deployment} = \mathbf{BLOCKED}, \quad \text{Confirmatory Execution} = \mathbf{PROHIBITED}$$

### The Immutability Rule
Under OSIRIS governance:
1. **The `FALSE` campaign is preserved permanently** as historical evidence object `evidence/operational/REAL-CAMPAIGN-20260926T160434Z/`.
2. It is **never deleted, modified, or overwritten**.
3. Subsequent remediation creates an entirely new campaign (`REAL-CAMPAIGN-<timestamp>`) with a new root hash, maintaining an unbroken audit trail:

```text
Configuration A (Baseline)
       │
       ▼
Deployment Campaign 001 (REAL-CAMPAIGN-20260926T160434Z)
       │
       └── FALSE (Seccomp mode 0, network egress, unconfined /tmp writes)
               │
               ▼
         Remediation & Hardening
               │
               ▼
Deployment Campaign 002 (REAL-CAMPAIGN-<new_timestamp>)
       │
       └── Independently adjudicated result
```

---

## 4. Cross-Plane Linking (XPL-2026-09-26-001)

The cross-plane record `XPL-2026-09-26-001` connects the failed operational deployment package (`OEP-REAL-CAMPAIGN-20260926T160434Z`) to the independent scientific research package (`SEP-QF-2026-001`) via the shared immutable software artifact:

```text
                            SHARED ARTIFACT
                   Git Commit: 8df61dc... / 8b3f047...
                   Artifact Digest: sha256:...
                                  │
                  ┌───────────────┴───────────────┐
                  │                               │
                  ▼                               ▼
      OPERATIONAL PACKAGE (OEP)         SCIENTIFIC PACKAGE (SEP)
      REAL-CAMPAIGN-20260926T160434Z    BlueQubit Flywheel QF-2026
      Root: sha256:fcb9...              Root: sha256:...
      Status: S_deployed = FALSE        Status: HYPOTHESES / SIMULATED
                  │                               │
                  └───────────────┬───────────────┘
                                  │
                                  ▼
                     CROSS-PLANE RECORD (XPL)
                     XPL-2026-09-26-001
                     Relationship: SAME_IMMUTABLE_ARTIFACT_LINEAGE
                     Substitution: STRICTLY FORBIDDEN
```

This ensures that an external auditor can truthfully verify:
> *"The research package references this exact software lineage, whose deployment campaign independently failed the confinement predicate."*

Neither the operational failure invalidates the theoretical scientific models, nor does scientific promise excuse the operational security failure.
