import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/persistence/local_storage.dart';

final localStorageProvider = Provider<LocalStorage>((ref) {
  throw UnimplementedError(
    'localStorageProvider must be overridden at app startup',
  );
});

class InitiationState {
  final bool isInitiated;
  final String? intention;

  const InitiationState({
    this.isInitiated = false,
    this.intention,
  });

  InitiationState copyWith({
    bool? isInitiated,
    String? intention,
  }) {
    return InitiationState(
      isInitiated: isInitiated ?? this.isInitiated,
      intention: intention ?? this.intention,
    );
  }
}

class InitiationNotifier extends StateNotifier<InitiationState> {
  final LocalStorage _storage;

  InitiationNotifier(this._storage) : super(const InitiationState()) {
    _load();
  }

  void _load() {
    state = InitiationState(
      isInitiated: _storage.isInitiated,
      intention: _storage.intention,
    );
  }

  Future<void> complete(String intention) async {
    _storage.isInitiated = true;
    _storage.intention = intention;
    state = state.copyWith(
      isInitiated: true,
      intention: intention,
    );
  }
}

final initiationProvider =
    StateNotifierProvider<InitiationNotifier, InitiationState>((ref) {
  return InitiationNotifier(
    ref.watch(localStorageProvider),
  );
});