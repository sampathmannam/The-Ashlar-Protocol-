import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

class LocalStorage {
  static LocalStorage? _instance;
  late SharedPreferences _prefs;

  LocalStorage._();

  static Future<LocalStorage> getInstance() async {
    if (_instance == null) {
      _instance = LocalStorage._();
      _instance!._prefs = await SharedPreferences.getInstance();
    }
    return _instance!;
  }

  // Initiation
  bool get isInitiated => _prefs.getBool('initiated') ?? false;
  set isInitiated(bool value) => _prefs.setBool('initiated', value);

  String? get intention => _prefs.getString('intention');
  set intention(String? value) {
    if (value != null) {
      _prefs.setString('intention', value);
    }
  }

  // Degree
  int get degreeIndex => _prefs.getInt('degree_index') ?? 0;
  set degreeIndex(int value) => _prefs.setInt('degree_index', value);

  // Streak
  int get stoneDays => _prefs.getInt('stone_days') ?? 0;
  set stoneDays(int value) => _prefs.setInt('stone_days', value);

  int get graceDaysRemaining => _prefs.getInt('grace_days') ?? 2;
  set graceDaysRemaining(int value) => _prefs.setInt('grace_days', value);

  DateTime? get lastWorkDate {
    final str = _prefs.getString('last_work_date');
    if (str != null) {
      return DateTime.tryParse(str);
    }
    return null;
  }

  set lastWorkDate(DateTime? value) {
    if (value != null) {
      _prefs.setString('last_work_date', value.toIso8601String());
    }
  }

  DateTime? get comebackDate {
    final str = _prefs.getString('comeback_date');
    if (str != null) {
      return DateTime.tryParse(str);
    }
    return null;
  }

  set comebackDate(DateTime? value) {
    if (value != null) {
      _prefs.setString('comeback_date', value.toIso8601String());
    }
  }

  // Tool counts
  int get plumbSessions => _prefs.getInt('plumb_sessions') ?? 0;
  set plumbSessions(int value) => _prefs.setInt('plumb_sessions', value);

  int get gaugeDays => _prefs.getInt('gauge_days') ?? 0;
  set gaugeDays(int value) => _prefs.setInt('gauge_days', value);

  int get recallSessions => _prefs.getInt('recall_sessions') ?? 0;
  set recallSessions(int value) => _prefs.setInt('recall_sessions', value);

  // Journal entries
  List<String> get journalEntries =>
      _prefs.getStringList('journal_entries') ?? [];

  Future<void> addJournalEntry(String entry) async {
    final entries = journalEntries;
    entries.add(jsonEncode({
      'text': entry,
      'date': DateTime.now().toIso8601String(),
    }));
    await _prefs.setStringList('journal_entries', entries);
  }

  // Chamber reflections
  List<String> get chamberReflections =>
      _prefs.getStringList('chamber_reflections') ?? [];

  Future<void> addChamberReflection(String reflection, bool keep) async {
    final reflections = chamberReflections;
    reflections.add(jsonEncode({
      'text': reflection,
      'date': DateTime.now().toIso8601String(),
      'keep': keep,
    }));
    await _prefs.setStringList('chamber_reflections', reflections);
  }

  // Daily mood
  int? get todayMood => _prefs.getInt('today_mood');
  set todayMood(int? value) {
    if (value != null) {
      _prefs.setInt('today_mood', value);
    }
  }

  DateTime? get moodDate {
    final str = _prefs.getString('mood_date');
    if (str != null) {
      return DateTime.tryParse(str);
    }
    return null;
  }

  set moodDate(DateTime? value) {
    if (value != null) {
      _prefs.setString('mood_date', value.toIso8601String());
    }
  }
}