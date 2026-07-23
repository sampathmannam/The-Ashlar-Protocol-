import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/persistence/local_storage.dart';
import '../stone/degrees.dart';
import '../initiation/initiation_provider.dart';

class ToolsState {
  final AshlarDegree degree;
  final int plumbSessions;
  final int gaugeDays;
  final int recallSessions;

  const ToolsState({
    this.degree = AshlarDegree.apprentice,
    this.plumbSessions = 0,
    this.gaugeDays = 0,
    this.recallSessions = 0,
  });
}

class ToolsNotifier extends StateNotifier<ToolsState> {
  final LocalStorage _storage;

  ToolsNotifier(this._storage) : super(const ToolsState()) {
    _load();
  }

  void _load() {
    final score = DegreesEngine.instance.score(
      plumbSessions: _storage.plumbSessions,
      gaugeDays: _storage.gaugeDays,
      recallSessions: _storage.recallSessions,
    );

    state = ToolsState(
      degree: DegreesEngine.instance.current(score),
      plumbSessions: _storage.plumbSessions,
      gaugeDays: _storage.gaugeDays,
      recallSessions: _storage.recallSessions,
    );
  }

  Future<void> recordPlumbSession() async {
    _storage.plumbSessions = _storage.plumbSessions + 1;
    _load();
  }

  Future<void> recordGaugeDay() async {
    _storage.gaugeDays = _storage.gaugeDays + 1;
    _load();
  }

  Future<void> recordRecallSession() async {
    _storage.recallSessions = _storage.recallSessions + 1;
    _load();
  }

  Future<void> recordWorkDay() async {
    final now = DateTime.now();
    final lastWork = _storage.lastWorkDate;

    if (lastWork == null || !_isSameDay(lastWork, now)) {
      _storage.stoneDays = _storage.stoneDays + 1;
      _storage.lastWorkDate = now;
    }
  }

  bool _isSameDay(DateTime a, DateTime b) {
    return a.year == b.year && a.month == b.month && a.day == b.day;
  }
}

final toolsProvider = StateNotifierProvider<ToolsNotifier, ToolsState>((ref) {
  return ToolsNotifier(
    ref.watch(localStorageProvider),
  );
});