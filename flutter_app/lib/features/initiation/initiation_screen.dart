import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../core/theme/ashlar_colors.dart';
import '../../core/crisis/crisis_support.dart';
import 'initiation_provider.dart';

class InitiationScreen extends ConsumerStatefulWidget {
  const InitiationScreen({super.key});

  @override
  ConsumerState<InitiationScreen> createState() => _InitiationScreenState();
}

class _InitiationScreenState extends ConsumerState<InitiationScreen> {
  final _intentionController = TextEditingController();
  final _pageController = PageController();
  int _currentPage = 0;

  @override
  void dispose() {
    _intentionController.dispose();
    _pageController.dispose();
    super.dispose();
  }

  void _nextPage() {
    if (_currentPage < 2) {
      _pageController.nextPage(
        duration: const Duration(milliseconds: 500),
        curve: Curves.easeInOut,
      );
    }
  }

  void _complete() {
    ref.read(initiationProvider.notifier).complete(
      _intentionController.text,
    );
    context.go('/board');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AshlarColors.charcoal,
      body: SafeArea(
        child: Column(
          children: [
            // Need Help button - always visible
            Align(
              alignment: Alignment.topRight,
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: GestureDetector(
                  onTap: () => CrisisSupportDialog.show(context),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: AshlarColors.redAlertSoft,
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(
                        color: AshlarColors.redAlert.withAlpha(100),
                      ),
                    ),
                    child: Text(
                      'NEED HELP?',
                      style: Theme.of(context).textTheme.labelSmall?.copyWith(
                        color: AshlarColors.redAlert,
                        fontSize: 10,
                      ),
                    ),
                  ),
                ),
              ),
            ),

            // Pages
            Expanded(
              child: PageView(
                controller: _pageController,
                physics: const NeverScrollableScrollPhysics(),
                onPageChanged: (index) {
                  setState(() => _currentPage = index);
                },
                children: [
                  _WelcomePage(onNext: _nextPage),
                  _IntentionPage(
                    controller: _intentionController,
                    onNext: _nextPage,
                  ),
                  _ThresholdPage(onComplete: _complete),
                ],
              ),
            ),

            // Progress dots
            Padding(
              padding: const EdgeInsets.only(bottom: 32),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: List.generate(3, (index) {
                  return AnimatedContainer(
                    duration: const Duration(milliseconds: 300),
                    margin: const EdgeInsets.symmetric(horizontal: 4),
                    width: index == _currentPage ? 24 : 8,
                    height: 8,
                    decoration: BoxDecoration(
                      color: index <= _currentPage
                          ? AshlarColors.gold
                          : AshlarColors.slate,
                      borderRadius: BorderRadius.circular(4),
                    ),
                  );
                }),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _WelcomePage extends StatelessWidget {
  final VoidCallback onNext;
  const _WelcomePage({required this.onNext});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(32),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            'THE ASHLAR',
            style: Theme.of(context).textTheme.headlineLarge?.copyWith(
              letterSpacing: 4,
            ),
          ),
          const SizedBox(height: 16),
          Text(
            'You are not broken.\nYou are unfinished.\nAnd there is a craft for the work.',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              color: AshlarColors.silver,
              height: 1.8,
            ),
          ),
          const SizedBox(height: 48),
          GestureDetector(
            onTap: onNext,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              decoration: BoxDecoration(
                color: AshlarColors.gold.withAlpha(30),
                borderRadius: BorderRadius.circular(32),
                border: Border.all(
                  color: AshlarColors.gold.withAlpha(100),
                ),
              ),
              child: Text(
                'BEGIN',
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                  color: AshlarColors.gold,
                  letterSpacing: 3,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _IntentionPage extends StatelessWidget {
  final TextEditingController controller;
  final VoidCallback onNext;

  const _IntentionPage({
    required this.controller,
    required this.onNext,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(32),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            'CHAMBER OF REFLECTION',
            style: Theme.of(context).textTheme.labelLarge?.copyWith(
              letterSpacing: 3,
            ),
          ),
          const SizedBox(height: 24),
          Text(
            'Before you begin the work,\ntake a moment to reflect.',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              color: AshlarColors.silver,
            ),
          ),
          const SizedBox(height: 32),
          Text(
            'What brings you here?\nWhat do you hope to become?',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 24),
          TextField(
            controller: controller,
            maxLines: 3,
            style: TextStyle(color: AshlarColors.lightText),
            decoration: InputDecoration(
              hintText: 'Write your intention...',
              hintStyle: TextStyle(color: AshlarColors.mutedText),
              filled: true,
              fillColor: AshlarColors.surface,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide(
                  color: AshlarColors.dividerWhite.withAlpha(80),
                ),
              ),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide(
                  color: AshlarColors.dividerWhite.withAlpha(80),
                ),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: const BorderSide(
                  color: AshlarColors.gold,
                ),
              ),
            ),
          ),
          const SizedBox(height: 32),
          GestureDetector(
            onTap: onNext,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              decoration: BoxDecoration(
                color: AshlarColors.gold.withAlpha(30),
                borderRadius: BorderRadius.circular(32),
                border: Border.all(
                  color: AshlarColors.gold.withAlpha(100),
                ),
              ),
              child: Text(
                'CONTINUE',
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                  color: AshlarColors.gold,
                  letterSpacing: 3,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _ThresholdPage extends StatelessWidget {
  final VoidCallback onComplete;
  const _ThresholdPage({required this.onComplete});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(32),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            'THE THRESHOLD',
            style: Theme.of(context).textTheme.labelLarge?.copyWith(
              letterSpacing: 3,
            ),
          ),
          const SizedBox(height: 24),
          Text(
            'You stand at the door.\nBehind you, the quarry.\nBefore you, the craft.',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              color: AshlarColors.silver,
              height: 1.8,
            ),
          ),
          const SizedBox(height: 32),
          Text(
            'The tools will come as you are ready.\nThe work begins with showing up.',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 48),
          GestureDetector(
            onTap: onComplete,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              decoration: BoxDecoration(
                color: AshlarColors.gold.withAlpha(30),
                borderRadius: BorderRadius.circular(32),
                border: Border.all(
                  color: AshlarColors.gold.withAlpha(100),
                ),
              ),
              child: Text(
                'ENTER THE CRAFT',
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                  color: AshlarColors.gold,
                  letterSpacing: 3,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}