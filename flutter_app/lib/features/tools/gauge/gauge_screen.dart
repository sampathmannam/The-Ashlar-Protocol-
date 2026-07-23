import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/ashlar_colors.dart';
import '../tools_provider.dart';

class GaugeScreen extends ConsumerStatefulWidget {
  const GaugeScreen({super.key});

  @override
  ConsumerState<GaugeScreen> createState() => _GaugeScreenState();
}

class _GaugeScreenState extends ConsumerState<GaugeScreen> {
  String? _selectedTask;
  bool _completed = false;

  static const _tasks = [
    'Make the bed.',
    'Wash one dish.',
    'Step outside for 60 seconds.',
    'Text one person.',
    'Put one thing where it belongs.',
    'Drink a glass of water.',
    'Stretch for 30 seconds.',
    'Open a window.',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AshlarColors.charcoal,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AshlarColors.silver),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'THE GAUGE',
          style: Theme.of(context).textTheme.labelLarge?.copyWith(
            letterSpacing: 2,
          ),
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'ONE SMALL ACT OF ORDER',
              style: Theme.of(context).textTheme.labelLarge?.copyWith(
                letterSpacing: 2,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'The gauge measures by doing, not by feeling.',
              style: Theme.of(context).textTheme.bodyMedium,
            ),
            const SizedBox(height: 24),

            if (!_completed) ...[
              // Task selection
              ...(_tasks.map(
                (task) => Padding(
                  padding: const EdgeInsets.only(bottom: 8),
                  child: GestureDetector(
                    onTap: () => setState(() => _selectedTask = task),
                    child: Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: _selectedTask == task
                            ? AshlarColors.gold.withAlpha(30)
                            : AshlarColors.surface,
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(
                          color: _selectedTask == task
                              ? AshlarColors.gold.withAlpha(80)
                              : AshlarColors.dividerWhite.withAlpha(60),
                        ),
                      ),
                      child: Row(
                        children: [
                          Container(
                            width: 24,
                            height: 24,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              border: Border.all(
                                color: _selectedTask == task
                                    ? AshlarColors.gold
                                    : AshlarColors.silver,
                              ),
                            ),
                            child: _selectedTask == task
                                ? Container(
                                    margin: const EdgeInsets.all(4),
                                    decoration: const BoxDecoration(
                                      shape: BoxShape.circle,
                                      color: AshlarColors.gold,
                                    ),
                                  )
                                : null,
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Text(
                              task,
                              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                                color: _selectedTask == task
                                    ? AshlarColors.lightText
                                    : AshlarColors.silver,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              )),
            ] else ...[
              // Completion
              Center(
                child: Column(
                  children: [
                    const SizedBox(height: 48),
                    Icon(
                      Icons.check_circle_outline,
                      color: AshlarColors.gold,
                      size: 64,
                    ),
                    const SizedBox(height: 24),
                    Text(
                      'THE GAUGE IS SET.',
                      style: Theme.of(context).textTheme.labelLarge?.copyWith(
                        letterSpacing: 2,
                      ),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'One act of order. The stone endures.',
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        color: AshlarColors.silver,
                      ),
                    ),
                  ],
                ),
              ),
            ],

            const Spacer(),

            if (!_completed && _selectedTask != null)
              GestureDetector(
                onTap: () {
                  ref.read(toolsProvider.notifier).recordGaugeDay();
                  ref.read(toolsProvider.notifier).recordWorkDay();
                  HapticFeedback.mediumImpact();
                  setState(() => _completed = true);
                },
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  decoration: BoxDecoration(
                    color: AshlarColors.gold.withAlpha(30),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: AshlarColors.gold.withAlpha(80),
                    ),
                  ),
                  child: Text(
                    'COMPLETE',
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.labelLarge?.copyWith(
                      letterSpacing: 2,
                    ),
                  ),
                ),
              ),

            if (_completed)
              GestureDetector(
                onTap: () => Navigator.pop(context),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  decoration: BoxDecoration(
                    color: AshlarColors.gold.withAlpha(30),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: AshlarColors.gold.withAlpha(80),
                    ),
                  ),
                  child: Text(
                    'RETURN',
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.labelLarge?.copyWith(
                      letterSpacing: 2,
                    ),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}