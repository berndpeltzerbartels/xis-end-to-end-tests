# Expression Language Test Matrix

This matrix separates parser completeness from user-facing browser confidence.

## Test Layers

| Layer | Repository | Purpose |
| --- | --- | --- |
| Parser/unit | `xis`, `xis-javascript` | Complete EL grammar and evaluation semantics. This is where edge cases belong. |
| XIS integration | `xis`, `xis-integration-tests` | XIS client/runtime behavior without real browser: DOM normalization, handlers, controller data. |
| E2E contract | `xis-end-to-end-tests` | Public examples in a real browser against platform runners. This should prove documented usage works, not duplicate every parser edge. |

## Coverage Matrix

| Feature | Parser/unit | XIS integration | E2E contract | Documentation | Next action |
| --- | --- | --- | --- | --- | --- |
| Plain variable access, `${name}` | Covered by `ExpressionParserTest`, `TextContentParserTest` | Covered indirectly | Added in `ExpressionLanguageE2ETest` | Needed | Keep |
| Dot property access, `user.name` | Covered | Covered indirectly | Added | Needed | Keep |
| Bracket property access, `user['name']` | Covered | Covered through computed bracket case | Covered through computed bracket case | Needed | Keep |
| Dynamic bracket property, `user[key]` | Covered | Covered through computed bracket case | Covered through computed bracket case | Needed | Keep |
| Computed bracket key, `xyz[x + '_item']` | Covered | Covered | Covered | Needed | Keep |
| Array index, `items[1]` | Covered | Covered by condition case | Covered by condition case | Useful | Keep |
| Computed array index, `items[offset + 1]` | Covered | Missing/unclear | Missing | Advanced | Parser coverage is enough unless documented |
| Arithmetic, `a + b * c` | Covered | Missing/unclear | Missing | Basic if expressions support operators publicly | Add one E2E assertion if documented |
| Comparison, `count > 0` | Covered | Covered through `xis:if` style tests | Covered | Needed | Keep |
| Boolean logic, `a && b`, `a || b`, `!a` | Covered | Covered partly | Covered | Needed | Keep |
| Parentheses/precedence | Covered | Missing/unclear | Missing | Basic | Parser coverage enough; one doc example may be enough |
| Ternary, `active ? 'yes' : 'no'` | Covered | Missing/unclear | Covered | Useful | Keep |
| Function call, `empty(value)` | Covered in `ELFunctionsTest` | Covered by `IfConditionTest`, `VisibleConditionTest` | Covered through `notEmpty()` | Needed | Keep |
| Nested function/expression parameters | Covered | Missing/unclear | Missing | Advanced | Parser coverage enough unless docs make it prominent |
| Method call, `text.toUpperCase()` | Covered | Missing | Missing | Decide public status | Document/test only if public |
| Chained method call | Covered | Missing | Missing | Advanced | Parser coverage enough unless public |
| Null-safe property/method behavior | Covered for method calls | Missing/unclear | Missing | Important if promised | Add integration coverage before E2E |
| Array literal, `[1, 2, 3]` | Covered | Missing | Missing | Advanced | Parser coverage enough |
| Text interpolation with multiple expressions | Covered by `TextContentParserTest` | Covered indirectly | Partly added | Needed | Add one E2E mixed-text assertion |
| Escaped interpolation | Covered by `TextContentParserTest` | Missing/unclear | Missing | Needed if documented | Add integration/E2E if documented |
| Attribute interpolation | Covered by parser/text content | Covered by handlers indirectly | Added | Needed | Keep |
| Body/head attribute interpolation | Handler support exists | Missing/unclear | Missing | Advanced | Integration first |
| `<xis:if>` tag syntax | Handler/integration covered | Covered by `IfConditionTest` | Covered | Needed | Keep |
| `xis:if` attribute syntax | Handler/integration covered | Covered by `IfConditionTest` | Added | Needed | Keep |
| `<xis:foreach>` tag syntax | Handler/integration covered | Covered by `ForeachTagPageTest` | Covered | Needed | Keep |
| `xis:foreach` attribute syntax | Handler/integration covered | Covered by `ForeachAttributePageTest` | Added | Needed | Keep |
| `xis:foreach` over expression array | Covered by `ForeachWithExprArrayTest` | Covered | Missing | Useful | E2E optional |
| Built-in `length()` | Covered | Missing/unclear | Missing | Needed if documented | Add E2E function group |
| Built-in `sum()` | Covered | Missing | Missing | Maybe advanced | Parser/unit enough unless documented |
| Built-in `map()` | Covered | Missing | Missing | Advanced | Parser/unit enough |
| Built-in `contains()` | Covered | Missing | Missing | Useful | Add E2E only if docs recommend it |
| Built-in `toUpperCase()`, `toLowerCase()` | Covered | Missing | Missing | Useful | Add E2E only if docs recommend it |
| Built-in `formatDate()` | Covered | Missing | Missing | Useful but date-sensitive | Prefer integration/unit; E2E only with stable date |
| Built-in `join()` | Covered | Covered by EL integration page | Covered | Useful | Keep |
| Built-in `filter()` | Covered | Missing | Missing | Advanced | Parser/unit enough |
| Built-in `empty()` / `isEmpty()` / `notEmpty()` | Covered/integration partly | Covered by `IfConditionTest` | Covered through `notEmpty()` | Needed | Keep |
| Built-in `defaultValue()` | Covered | Covered by EL integration page | Covered | Useful | Keep |

## Working Rule

Parser/unit tests should be exhaustive. XIS integration tests should prove each EL feature works through the relevant handler or normalizer. E2E tests should cover documented examples and common user paths. A feature should appear in E2E when a user is likely to copy it from the documentation.

## Immediate Gaps

1. Keep parser/unit coverage as the place for expression edge cases such as nested functions, nested arrays, and operator precedence.
2. Keep the E2E expression page aligned with examples that appear in the user documentation.
3. Decide whether JavaScript method calls are public EL API. If yes, document and add at least integration coverage.
4. Document `xis:foreach` attribute syntax carefully: it repeats the host element content. Use `<xis:foreach>` or `xis:repeat` when attributes of the repeated element must depend on the loop item.
