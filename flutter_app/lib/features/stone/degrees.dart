enum AshlarDegree {
  apprentice,
  fellowcraft,
  masterMason,
}

extension AshlarDegreeExtension on AshlarDegree {
  String get name {
    switch (this) {
      case AshlarDegree.apprentice:
        return 'Apprentice';
      case AshlarDegree.fellowcraft:
        return 'Fellowcraft';
      case AshlarDegree.masterMason:
        return 'Master Mason';
    }
  }

  String get title {
    switch (this) {
      case AshlarDegree.apprentice:
        return 'Arriving';
      case AshlarDegree.fellowcraft:
        return 'Building';
      case AshlarDegree.masterMason:
        return 'Integrating';
    }
  }

  String get description {
    switch (this) {
      case AshlarDegree.apprentice:
        return 'Accept the rough stone. You are unfinished, not broken.';
      case AshlarDegree.fellowcraft:
        return 'Work the mind. Straighten a leaning thought.';
      case AshlarDegree.masterMason:
        return 'Meaning, mortality, and turning outward.';
    }
  }

  List<String> get tools {
    switch (this) {
      case AshlarDegree.apprentice:
        return ['gauge', 'gavel', 'breath_pacer'];
      case AshlarDegree.fellowcraft:
        return ['plumb', 'level', 'mouth_to_ear'];
      case AshlarDegree.masterMason:
        return ['relief', 'reach_out'];
    }
  }

  bool get isApprentice => this == AshlarDegree.apprentice;
  bool get isFellowcraft => this == AshlarDegree.fellowcraft;
  bool get isMasterMason => this == AshlarDegree.masterMason;
}

class DegreesEngine {
  static DegreesEngine? _instance;
  DegreesEngine._();

  static DegreesEngine get instance {
    _instance ??= DegreesEngine._();
    return _instance!;
  }

  int score({
    required int plumbSessions,
    required int gaugeDays,
    required int recallSessions,
  }) {
    return (plumbSessions * 3) + (gaugeDays * 2) + (recallSessions * 1);
  }

  AshlarDegree current(int score) {
    if (score >= 15) return AshlarDegree.masterMason;
    if (score >= 5) return AshlarDegree.fellowcraft;
    return AshlarDegree.apprentice;
  }

  double progress(int score) {
    if (score >= 15) return 1.0;
    if (score >= 5) {
      return (score - 5) / 10.0;
    }
    return score / 5.0;
  }
}