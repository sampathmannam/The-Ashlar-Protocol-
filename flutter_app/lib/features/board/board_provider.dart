import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/persistence/local_storage.dart';
import '../stone/degrees.dart';
import '../initiation/initiation_provider.dart';

class BoardState {
  final AshlarDegree degree;
  final int score;
  final double progress;
  final int stoneDays;
  final bool isComeback;
  final int wisdomScore;
  final int strengthScore;
  final int beautyScore;
  final String dailyPrompt;

  const BoardState({
    this.degree = AshlarDegree.apprentice,
    this.score = 0,
    this.progress = 0.0,
    this.stoneDays = 0,
    this.isComeback = false,
    this.wisdomScore = 0,
    this.strengthScore = 0,
    this.beautyScore = 0,
    this.dailyPrompt = 'Take one breath. Notice one thing. Show up for the work.',
  });
}

class BoardNotifier extends StateNotifier<BoardState> {
  final LocalStorage _storage;

  BoardNotifier(this._storage) : super(const BoardState()) {
    _load();
  }

  void _load() {
    final score = DegreesEngine.instance.score(
      plumbSessions: _storage.plumbSessions,
      gaugeDays: _storage.gaugeDays,
      recallSessions: _storage.recallSessions,
    );

    final degree = DegreesEngine.instance.current(score);
    final progress = DegreesEngine.instance.progress(score);

    final now = DateTime.now();
    final lastWork = _storage.lastWorkDate;
    bool isComeback = false;

    if (lastWork != null) {
      final daysSince = now.difference(lastWork).inDays;
      if (daysSince > 1) {
        isComeback = true;
        _storage.comebackDate = now;
      }
    }

    state = BoardState(
      degree: degree,
      score: score,
      progress: progress,
      stoneDays: _storage.stoneDays,
      isComeback: isComeback,
      wisdomScore: _storage.plumbSessions * 3,
      strengthScore: _storage.gaugeDays * 2,
      beautyScore: _storage.recallSessions,
    );
  }

  void refresh() => _load();
}

final boardProvider = StateNotifierProvider<BoardNotifier, BoardState>((ref) {
  return BoardNotifier(
    ref.watch(localStorageProvider),
  );
});