class CrisisDetector {
  static final CrisisDetector instance = CrisisDetector._();
  CrisisDetector._();

  static const _crisisPatterns = [
    'kill myself',
    'end my life',
    'want to die',
    'suicide',
    'suicidal',
    'harm myself',
    'hurt myself',
    'cut myself',
    'overdose',
    'no reason to live',
    'better off dead',
    'end it all',
    'not worth living',
    'wish i was dead',
    'going to kill',
    'planning to die',
    'final goodbye',
    'last message',
    'goodbye forever',
  ];

  static const _riskPatterns = [
    'hopeless',
    'worthless',
    'useless',
    'burden',
    'cant go on',
    'cant take it',
    'give up',
    'no way out',
    'trapped',
    'suffering',
    'pain',
    'alone',
    'isolated',
    'empty',
    'numb',
    'broken',
    'lost',
  ];

  CrisisResult analyze(String text) {
    final lower = text.toLowerCase();

    for (final pattern in _crisisPatterns) {
      if (lower.contains(pattern)) {
        return CrisisResult(
          level: CrisisLevel.crisis,
          matchedPattern: pattern,
        );
      }
    }

    for (final pattern in _riskPatterns) {
      if (lower.contains(pattern)) {
        return CrisisResult(
          level: CrisisLevel.elevated,
          matchedPattern: pattern,
        );
      }
    }

    return CrisisResult(level: CrisisLevel.none);
  }
}

enum CrisisLevel { none, elevated, crisis }

class CrisisResult {
  final CrisisLevel level;
  final String? matchedPattern;

  const CrisisResult({
    required this.level,
    this.matchedPattern,
  });

  bool get isSafe => level == CrisisLevel.none;
  bool get isElevated => level == CrisisLevel.elevated;
  bool get isCrisis => level == CrisisLevel.crisis;
}