package ru.performanceugc.lr3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.performanceugc.lr3.domain.model.OWN_BUSINESS
import ru.performanceugc.lr3.ui.brief.BriefScreen
import ru.performanceugc.lr3.ui.brief.BriefViewModel
import ru.performanceugc.lr3.ui.business.BusinessDetails
import ru.performanceugc.lr3.ui.business.BusinessDirectory
import ru.performanceugc.lr3.ui.business.BusinessEditor
import ru.performanceugc.lr3.ui.business.BusinessViewModel
import ru.performanceugc.lr3.ui.catalog.CatalogScreen
import ru.performanceugc.lr3.ui.catalog.CatalogViewModel
import ru.performanceugc.lr3.ui.home.HomeScreen
import ru.performanceugc.lr3.ui.home.HomeViewModel
import ru.performanceugc.lr3.ui.profile.ProfileScreen
import ru.performanceugc.lr3.ui.profile.ProfileViewModel
import ru.performanceugc.lr3.ui.profile.ProjectScreen

private const val HOME = "home"

private const val BRIEF = "brief"

private const val CATALOG = "creators?briefId={briefId}"

private const val PROFILE = "creator/{creatorId}?briefId={briefId}"

@Composable
internal fun StudioNavigation() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = HOME) {
        composable(HOME) {
            val model: HomeViewModel = hiltViewModel()
            val brief by model.latestBrief.collectAsStateWithLifecycle()
            HomeScreen(
                brief = brief,
                onBrowse = { nav.navigate("creators") { launchSingleTop = true } },
                onBusinesses = { nav.navigate("businesses") { launchSingleTop = true } },
                onAccount = { nav.navigate("account") { launchSingleTop = true } },
                onNew = { nav.navigate(BRIEF) { launchSingleTop = true } },
                onOpen = { id -> nav.navigate("creators?briefId=$id") { launchSingleTop = true } },
            )
        }
        composable(BRIEF) {
            val model: BriefViewModel = hiltViewModel()
            val state by model.state.collectAsStateWithLifecycle()
            BriefScreen(state, { nav.popBackStack() }, model::update, model::fillExample) {
                model.submit()?.let { id -> nav.navigate("creators?briefId=$id") { launchSingleTop = true } }
            }
        }
        composable(CATALOG, arguments = listOf(navArgument(BRIEF_ID) { type = NavType.StringType; nullable = true; defaultValue = null })) {
            val model: CatalogViewModel = hiltViewModel()
            val state by model.state.collectAsStateWithLifecycle()
            CatalogScreen(
                state = state,
                browsing = it.arguments?.getString(BRIEF_ID) == null,
                onBack = { nav.popBackStack() },
                onQuery = model::updateQuery,
                onCategory = model::selectCategory,
                onBudget = model::toggleBudget,
                onReset = model::resetFilters,
                onCreator = { brief, creator ->
                    val route = "creator/$creator" + (brief?.let { "?briefId=$it" } ?: "")
                    nav.navigate(route) { launchSingleTop = true }
                },
            )
        }
        composable(
            PROFILE,
            arguments = listOf(
                navArgument(BRIEF_ID) { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument(CREATOR_ID) { type = NavType.StringType },
            ),
        ) {
            val model: ProfileViewModel = hiltViewModel()
            val state by model.state.collectAsStateWithLifecycle()
            ProfileScreen(state, it.arguments?.getString(BRIEF_ID) == null, { nav.popBackStack() }, model::confirm, { nav.popBackStack(HOME, false) }) { project ->
                nav.navigate("work/${state.creator?.id}/$project") { launchSingleTop = true }
            }
        }
        composable("work/{creatorId}/{projectId}", arguments = listOf(
            navArgument(CREATOR_ID) { type = NavType.StringType },
            navArgument("projectId") { type = NavType.StringType },
        )) {
            val model: ProfileViewModel = hiltViewModel()
            ProjectScreen(model.project) { nav.popBackStack() }
        }
        composable("businesses") {
            val model: BusinessViewModel = hiltViewModel()
            val businesses by model.businesses.collectAsStateWithLifecycle()
            BusinessDirectory(businesses, { nav.popBackStack() }) { id -> nav.navigate("business/$id") }
        }
        composable("business/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
            val model: BusinessViewModel = hiltViewModel()
            val businesses by model.businesses.collectAsStateWithLifecycle()
            BusinessDetails(businesses.find { it.id == entry.arguments?.getString("id") }, { nav.popBackStack() }) {
                nav.navigate("account")
            }
        }
        composable("account") {
            val model: BusinessViewModel = hiltViewModel()
            val draft by model.draft.collectAsStateWithLifecycle()
            BusinessEditor(draft, { nav.popBackStack() }, model::update) {
                model.save()
                if (nav.previousBackStackEntry?.destination?.route == "business/{id}") {
                    nav.popBackStack()
                } else {
                    nav.navigate("business/$OWN_BUSINESS") { popUpTo("account") { inclusive = true } }
                }
            }
        }
    }
}
