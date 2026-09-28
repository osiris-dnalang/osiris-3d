# Executive Summary: BlueQubit Quantum Flywheel Grant 2026

**Project:** OSIRIS / LivLM: Evidence-Governed Adversarial Verification of Quantum Advantage Claims  
**Grant Program:** BlueQubit Quantum Flywheel $150K Sponsorship Program (2026)  
**Primary Track:** Track 2 — Breaking Quantum Advantage Claims  
**Hardware & Seed Component:** Track 1 (IBM Heron QPU Access) & Track 3-Adjacent (Scientific Reproducibility & Governance)  
**Lead Investigator:** Devin Phillip Davis, Agile Defense Systems LLC (`osiris.dnalang@gmail.com`)  
**Lead Repositories:** [osiris-dnalang/flywheel-2026](https://github.com/osiris-dnalang/flywheel-2026) · [osiris-dnalang/osiris-governance](https://github.com/osiris-dnalang/osiris-governance)  
**Zenodo Anchors:** [10.5281/zenodo.22863245](https://doi.org/10.5281/zenodo.22863245) (Proposal) · [10.5281/zenodo.22980008](https://doi.org/10.5281/zenodo.22980008) (Software & Evidence)  
**Release Tag:** `v0.1.0-beta.1` (Reference Implementation)  

---

## 1. The Core Scientific Premise

The objective of this project is **not** to manufacture another premature quantum advantage claim. It is:

> **To build an adversarial, provenance-preserving system for stress-testing and attempting to break claimed quantum advantages through classical simulation, controlled hardware confrontation, and auditable evidence ledgers—publishing the result whether the claim survives or fails.**

We address one falsifiable research question:

> **"For a declared family of NISQ circuits, does a claimed quantum-performance advantage survive adversarial classical simulation, independent reproduction, and hardware-versus-classical comparison under a preregistered protocol?"**

---

## 2. Positioning LivLM: Orchestration Layer, Not Scientific Authority

In this architecture, the Living Language Model (LivLM) is **not** an auto-regressive chatbot granted authority to declare scientific victory. It is the **intent-to-experiment governance and orchestration layer**:

```text
Researcher Intent
       │
       ▼
LivLM / OSIRIS Control Plane
       │
       ├── Formalizes research intent into typed proposals
       ├── Generates immutable experiment specifications
       ├── Creates SHA-256 provenance manifests
       ├── Dispatches classical attacks (MPS, Pauli-path, heuristics)
       ├── Gates QPU hardware execution behind strict evidence checks
       └── Produces tamper-evident evidence packages
                 │
                 ▼
     Independent Scientific Adjudication
```

> **The Architectural Rule:**  
> *The AI proposes and orchestrates; deterministic tooling measures; evidence packages preserve; adjudication decides.*

---

## 3. The 4-Level Adversarial Attack Ladder (Track 2)

Rather than evaluating circuits against a single classical simulator, our pipeline mounts an escalating four-level adversarial campaign to establish whether quantum execution provides authentic advantage over classical computation:

1. **Attack Level 0 — Exact Small-Instance Reference:**  
   Validate the validators on small circuits ($M \le 24$ qubits) using exact classical statevector simulation. If the pipeline cannot reproduce small instances with zero error, execution halts immediately.
2. **Attack Level 1 — Tensor-Network Simulation (MPS / PEPS):**  
   Simulate active circuit families using Matrix Product States via BlueQubit's GPU simulators, measuring fidelity as a function of bond dimension $\chi \in [16, 256]$ and entanglement entropy.
3. **Attack Level 2 — Pauli-Path & Hybrid Expansions:**  
   Attack the benchmark using Clifford+T decomposition and Pauli-path truncation, establishing classical resource boundaries across low-T-depth subspaces.
4. **Attack Level 3 — Heuristic & Evolutionary Search:**  
   Allow the LivLM controller to synthesize candidate classical approximation strategies (symmetry exploitation, circuit simplification). The benchmark independently evaluates each attack.

---

## 4. The Three Orthogonal Workstreams

The project cleanly separates operational execution from scientific claims via three distinct evidence planes:

- **Workstream A — Operational Reproducibility (OEP):**  
  *Question:* "Can every computational step be attributed to an immutable artifact, runtime, policy, and execution interval?"  
  *Deliverables:* Immutable Operational Evidence Packages (`OEP-001`, `OEP-002`, ...). Does **not** establish scientific validity.
- **Workstream B — Scientific Adversarial Testing (SEP):**  
  *Question:* "Does the target quantum claim survive independent classical attacks and controlled reproduction?"  
  *Deliverables:* Scientific Evidence Packages (`SEP-001`, `SEP-002`, ...) containing raw bitstrings, coverage-adjusted estimators (Miller-Madow, NSB, Chao-Shen), and falsification verdicts.
- **Workstream C — Cross-Plane Provenance (XPL):**  
  *Question:* "Can an independent reviewer prove exactly which software artifact generated each result?"  
  *Deliverables:* Cross-Plane Provenance records (`XPL-001`, `XPL-002`, ...) enforcing strict non-substitution:
  $$\text{SCIENCE} \not\vdash \text{DEPLOYMENT}, \quad \text{DEPLOYMENT} \not\vdash \text{SCIENCE}$$

---

## 5. Three-Month Research Plan & Phased Gates

BlueQubit's Quantum Flywheel provides 3-month research access within an overall program framework. We structure the campaign across three concrete verification gates:

### Month 1 — Classical Attack Surface
- **Weeks 1–2:** Pre-register target claim families, baseline metrics, and refutation criteria (Zenodo versioned); establish Level 0 exact baselines.
- **Weeks 3–4:** Execute Level 1 tensor-network sweeps across bond dimensions; run Level 2 Pauli-path attacks.
- **Gate 1: Classical Boundary Report.** Complete characterization of classical simulation boundaries, resource curves, and preliminary refutations.

### Month 2 — Hardware Confrontation
- **Weeks 5–6:** Freeze circuit specifications and pre-execution manifests; submit jobs to IBM Heron 156-qubit QPU via BlueQubit API.
- **Weeks 7–8:** Perform hardware-versus-classical comparison under identical error-mitigation and measurement protocols.
- **Gate 2: Quantum Execution Provenance Audit.** Publish signed hardware receipts, calibration hashes, raw counts, and cross-plane links.

### Month 3 — Adversarial Self-Attacking & Adjudication
- **Weeks 9–10:** Ask: *"What would convince us that our own result is wrong?"* Mount adversarial counter-attacks against the strongest observed result.
- **Weeks 11–12:** Blind independent adjudication; compile final benchmark suite, open data records (CC-BY), and preprint.
- **Gate 3: Independent Adjudication Report.** Final verdict: `SUPPORTED`, `REFUTED`, or `INCONCLUSIVE`.

---

## 6. What Makes This Proposal Unique

This proposal does not request hardware credits to claim an unearned victory. It requests resources to demonstrate **how to break claims rigorously**, with:
- Zero mutable artifact references;
- Pre-registered falsification criteria committed before the first job runs;
- The **Evidence Non-Substitution Invariant** preventing software passes from masquerading as physical hardware discoveries; and
- A proven open-science track record of self-refuting artifacts when empirical data dictates.
