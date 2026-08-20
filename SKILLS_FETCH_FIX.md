# Technical skills dashboard fix

The candidate profile DTO and service now persist and return `technicalSkills`.

For an existing candidate whose skills were previously not saved:
1. Log in as that candidate.
2. Open the Education step.
3. Select the skills again if needed.
4. Click Save/Continue.
5. Refresh the HR dashboard.

New and updated candidate profiles will store skills in `candidate_technical_skills`.
