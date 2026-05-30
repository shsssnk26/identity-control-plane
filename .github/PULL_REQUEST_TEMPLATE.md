## 📋 Summary

<!-- What does this PR do? One paragraph max. -->

## 🔗 Linked Issue

Closes #<!-- issue number -->

## 🧩 Type of Change

- [ ] ✨ New feature
- [ ] 🐛 Bug fix
- [ ] ♻️ Refactor (no behavior change)
- [ ] 🔐 Security fix
- [ ] 📝 Docs / comments
- [ ] 🧪 Tests only
- [ ] ⚙️ CI / config

## 🔐 Security Checklist

<!-- Required for auth/authz/token changes. Skip for pure docs/CI PRs. -->

- [ ] No secrets, tokens, or credentials in code or logs
- [ ] Input is validated before use (SQL params, user-supplied data)
- [ ] Auth is enforced on new endpoints (`@PreAuthorize` / Spring Security)
- [ ] Token expiry / refresh logic is correct (no infinite sessions)
- [ ] Password hashing uses BCrypt (no plain-text, no MD5/SHA1)
- [ ] New permissions added to RBAC roles where needed

## 🧪 Test Coverage

- [ ] Unit tests added / updated
- [ ] Integration tests added / updated (if applicable)
- [ ] All existing tests pass locally (`./gradlew test`)
- [ ] Manual smoke test done

## 📸 How to Test

<!-- Step-by-step so a reviewer can reproduce without guessing. -->

1. 
2. 
3. 

## 📝 Notes for Reviewer

<!-- Anything tricky, design decisions, known trade-offs, TODO follow-ups. -->
