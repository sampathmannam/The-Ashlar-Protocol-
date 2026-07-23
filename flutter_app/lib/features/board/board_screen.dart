import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/theme/ashlar_colors.dart';
import '../stone/degrees.dart';
import 'board_provider.dart';

class BoardScreen extends ConsumerWidget {
  const BoardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final boardState = ref.watch(boardProvider);

    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Ashlar visualization
          _AshlarVisualization(
            degree: boardState.degree,
            progress: boardState.progress,
          ),
          const SizedBox(height: 32),

          // Degree card
          _DegreeCard(
            degree: boardState.degree,
            score: boardState.score,
          ),
          const SizedBox(height: 24),

          // Pillars row
          Row(
            children: [
              Expanded(
                child: _PillarCard(
                  title: 'WISDOM',
                  value: boardState.wisdomScore,
                  icon: Icons.psychology_outlined,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _PillarCard(
                  title: 'STRENGTH',
                  value: boardState.strengthScore,
                  icon: Icons.fitness_center_outlined,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _PillarCard(
                  title: 'BEAUTY',
                  value: boardState.beautyScore,
                  icon: Icons.auto_awesome_outlined,
                ),
              ),
            ],
          ),
          const SizedBox(height: 24),

          // Streak
          _StreakCard(
            stoneDays: boardState.stoneDays,
            isComeback: boardState.isComeback,
          ),
          const SizedBox(height: 24),

          // Daily prompt
          _DailyPromptCard(
            prompt: boardState.dailyPrompt,
          ),
        ],
      ),
    );
  }
}

class _AshlarVisualization extends StatelessWidget {
  final AshlarDegree degree;
  final double progress;

  const _AshlarVisualization({
    required this.degree,
    required this.progress,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: 200,
        height: 200,
        child: Stack(
          alignment: Alignment.center,
          children: [
            // Progress ring
            SizedBox(
              width: 200,
              height: 200,
              child: CircularProgressIndicator(
                value: progress,
                strokeWidth: 4,
                backgroundColor: AshlarColors.slate,
                valueColor: const AlwaysStoppedAnimation<Color>(
                  AshlarColors.gold,
                ),
              ),
            ),
            // Ashlar icon
            Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(
                  Icons.diamond_outlined,
                  size: 64,
                  color: degree.isApprentice
                      ? AshlarColors.silver
                      : degree.isFellowcraft
                          ? AshlarColors.goldLight
                          : AshlarColors.gold,
                ),
                const SizedBox(height: 8),
                Text(
                  degree.title,
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                    letterSpacing: 2,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _DegreeCard extends StatelessWidget {
  final AshlarDegree degree;
  final int score;

  const _DegreeCard({
    required this.degree,
    required this.score,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AshlarColors.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AshlarColors.dividerWhite.withAlpha(60),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                degree.isApprentice
                    ? Icons.construction_outlined
                    : degree.isFellowcraft
                        ? Icons.architecture_outlined
                        : Icons.workspace_premium_outlined,
                color: AshlarColors.gold,
                size: 20,
              ),
              const SizedBox(width: 8),
              Text(
                degree.name.toUpperCase(),
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                  letterSpacing: 2,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            degree.description,
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 16),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Mastery Score',
                style: Theme.of(context).textTheme.bodySmall,
              ),
              Text(
                '$score',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AshlarColors.gold,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _PillarCard extends StatelessWidget {
  final String title;
  final int value;
  final IconData icon;

  const _PillarCard({
    required this.title,
    required this.value,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AshlarColors.surface,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: AshlarColors.dividerWhite.withAlpha(40),
        ),
      ),
      child: Column(
        children: [
          Icon(
            icon,
            color: AshlarColors.gold,
            size: 24,
          ),
          const SizedBox(height: 8),
          Text(
            title,
            style: Theme.of(context).textTheme.labelSmall?.copyWith(
              fontSize: 9,
              letterSpacing: 1,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '$value',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: AshlarColors.lightText,
            ),
          ),
        ],
      ),
    );
  }
}

class _StreakCard extends StatelessWidget {
  final int stoneDays;
  final bool isComeback;

  const _StreakCard({
    required this.stoneDays,
    required this.isComeback,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AshlarColors.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AshlarColors.dividerWhite.withAlpha(60),
        ),
      ),
      child: Row(
        children: [
          Container(
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              color: AshlarColors.gold.withAlpha(30),
              borderRadius: BorderRadius.circular(24),
            ),
            child: Icon(
              isComeback ? Icons.replay : Icons.diamond_outlined,
              color: AshlarColors.gold,
              size: 24,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  isComeback ? 'WELCOME BACK' : 'TENDING THE STONE',
                  style: Theme.of(context).textTheme.labelSmall?.copyWith(
                    letterSpacing: 1,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  '$stoneDays day${stoneDays == 1 ? '' : 's'} of practice',
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    color: AshlarColors.lightText,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _DailyPromptCard extends StatelessWidget {
  final String prompt;

  const _DailyPromptCard({required this.prompt});

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
            'DAILY WORKING',
            style: Theme.of(context).textTheme.labelSmall?.copyWith(
              letterSpacing: 1,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            prompt,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              color: AshlarColors.lightText,
              height: 1.5,
            ),
          ),
        ],
      ),
    );
  }
}