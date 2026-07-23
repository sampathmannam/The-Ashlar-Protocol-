import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/persistence/local_storage.dart';
import '../initiation/initiation_provider.dart';

class ChamberReflection {
  final String text;
  final DateTime date;
  final bool keep;

  const ChamberReflection({
    required this.text,
    required this.date,
    required this.keep,
  });

  factory ChamberReflection.fromJson(Map<String, dynamic> json) {
    return ChamberReflection(
      text: json['text'] ?? '',
      date: DateTime.parse(json['date'] ?? DateTime.now().toIso8601String()),
      keep: json['keep'] ?? false,
    );
  }
}

class ChamberState {
  final List<ChamberReflection> keptReflections;

  const ChamberState({
    this.keptReflections = const [],
  });
}

class ChamberNotifier extends StateNotifier<ChamberState> {
  final LocalStorage _storage;

  ChamberNotifier(this._storage) : super(const ChamberState()) {
    _load();
  }

  void _load() {
    final reflections = _storage.chamberReflections;
    final kept = <ChamberReflection>[];

    for (final json in reflections) {
      try {
        final data = Map<String, dynamic>.from(jsonDecode(json));
        final reflection = ChamberReflection.fromJson(data);
        if (reflection.keep) {
          kept.add(reflection);
        }
      } catch (_) {
        // Skip malformed entries
      }
    }

    state = ChamberState(keptReflections: kept);
  }

  Future<void> addReflection(String text, {required bool keep}) async {
    await _storage.addChamberReflection(text, keep);
    _load();
  }
}

final chamberProvider =
    StateNotifierProvider<ChamberNotifier, ChamberState>((ref) {
  return ChamberNotifier(
    ref.watch(localStorageProvider),
  );
});