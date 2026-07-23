import 'package:flutter/material.dart';
import '../../../core/theme/ashlar_colors.dart';

class DailyWordCard extends StatelessWidget {
  const DailyWordCard({super.key});

  static const _words = [
    'Patience',
    'Courage',
    'Wisdom',
    'Strength',
    'Beauty',
    'Harmony',
    'Truth',
    'Justice',
    'Mercy',
    'Faith',
    'Hope',
    'Charity',
    'Prudence',
    'Temperance',
    'Fortitude',
    'Diligence',
    'Kindness',
    'Humility',
    'Perseverance',
    'Integrity',
  ];

  String get _todayWord {
    final dayOfYear = DateTime.now().difference(DateTime(DateTime.now().year)).inDays;
    return _words[dayOfYear % _words.length];
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AshlarColors.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AshlarColors.gold.withAlpha(40),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'DAILY WORD',
            style: Theme.of(context).textTheme.labelSmall?.copyWith(
              letterSpacing: 1,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            _todayWord.toUpperCase(),
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              color: AshlarColors.gold,
              letterSpacing: 4,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Carry this word through the day.',
            style: Theme.of(context).textTheme.bodySmall,
          ),
        ],
      ),
    );
  }
}